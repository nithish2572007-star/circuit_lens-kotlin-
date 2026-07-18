#include "httplib.h"
#include "json.hpp"
#include <iostream>
#include <vector>
#include <string>
#include <map>
#include <cmath>
#include <algorithm>

using json = nlohmann::json;

bool solve_system(std::vector<std::vector<double>>& A, std::vector<double>& B, std::vector<double>& x) {
    int n = A.size();
    x.assign(n, 0.0);
    
    // Forward elimination
    for (int i = 0; i < n; i++) {
        // Find pivot
        double maxEl = std::abs(A[i][i]);
        int maxRow = i;
        for (int k = i + 1; k < n; k++) {
            if (std::abs(A[k][i]) > maxEl) {
                maxEl = std::abs(A[k][i]);
                maxRow = k;
            }
        }
        
        // Swap maximum row with current row
        if (maxRow != i) {
            std::swap(A[maxRow], A[i]);
            std::swap(B[maxRow], B[i]);
        }
        
        // Check for singular matrix
        if (std::abs(A[i][i]) < 1e-12) {
            return false; // Singular matrix
        }
        
        // Eliminate column below
        for (int k = i + 1; k < n; k++) {
            double c = -A[k][i] / A[i][i];
            for (int j = i; j < n; j++) {
                if (i == j) {
                    A[k][j] = 0.0;
                } else {
                    A[k][j] += c * A[i][j];
                }
            }
            B[k] += c * B[i];
        }
    }
    
    // Back substitution
    for (int i = n - 1; i >= 0; i--) {
        x[i] = B[i] / A[i][i];
        for (int k = i - 1; k >= 0; k--) {
            B[k] -= A[k][i] * x[i];
        }
    }
    return true;
}

int main() {
    httplib::Server svr;

    svr.Post("/simulate", [](const httplib::Request& req, httplib::Response& res) {
        res.set_header("Access-Control-Allow-Origin", "*");
        res.set_header("Content-Type", "application/json");

        try {
            json root = json::parse(req.body);
            if (!root.contains("components") || !root["components"].is_array()) {
                res.status = 400;
                res.body = json{{"status", "error"}, {"message", "Missing 'components' array"}}.dump();
                return;
            }

            // 1. Discover all unique node IDs
            std::vector<std::string> node_ids;
            node_ids.push_back("0"); // Ensure ground node is index 0

            for (const auto& comp : root["components"]) {
                if (comp.contains("pins") && comp["pins"].is_array()) {
                    for (const auto& pin : comp["pins"]) {
                        if (pin.contains("nodeId")) {
                            std::string nid = pin["nodeId"].get<std::string>();
                            if (std::find(node_ids.begin(), node_ids.end(), nid) == node_ids.end()) {
                                node_ids.push_back(nid);
                            }
                        }
                    }
                }
            }

            // Map nodeId to dense index
            std::map<std::string, int> node_map;
            for (size_t i = 0; i < node_ids.size(); ++i) {
                node_map[node_ids[i]] = i;
            }

            int num_nodes = node_ids.size();
            int N = num_nodes - 1; // Number of non-ground nodes

            // Count voltage sources
            int M = 0;
            std::vector<json> volt_sources;
            for (const auto& comp : root["components"]) {
                std::string type = comp.value("type", "");
                if (type == "DC_VOLTAGE") {
                    volt_sources.push_back(comp);
                    M++;
                }
            }

            int sys_size = N + M;
            if (sys_size <= 0) {
                res.status = 200;
                res.body = json{{"status", "success"}, {"voltages", json::object()}, {"currents", json::object()}}.dump();
                return;
            }

            // Build equations
            std::vector<std::vector<double>> A(sys_size, std::vector<double>(sys_size, 0.0));
            std::vector<double> B(sys_size, 0.0);

            // Populate resistors (and capacitors/inductors modeled in DC steady-state)
            for (const auto& comp : root["components"]) {
                std::string type = comp.value("type", "");
                double value = comp.value("value", 0.0);
                if (comp.contains("pins") && comp["pins"].is_array() && comp["pins"].size() >= 2) {
                    std::string n1_id = comp["pins"][0].value("nodeId", "0");
                    std::string n2_id = comp["pins"][1].value("nodeId", "0");
                    int u = node_map[n1_id];
                    int v = node_map[n2_id];

                    if (type == "RESISTOR") {
                        double G = 1.0 / (value > 0 ? value : 1e-12);
                        if (u > 0) A[u-1][u-1] += G;
                        if (v > 0) A[v-1][v-1] += G;
                        if (u > 0 && v > 0) {
                            A[u-1][v-1] -= G;
                            A[v-1][u-1] -= G;
                        }
                    } else if (type == "CAPACITOR") {
                        // Capacitor is open circuit in DC steady-state (conductance = 0)
                        // Do nothing
                    } else if (type == "INDUCTOR") {
                        // Inductor is short circuit in DC steady-state (R = 1e-6 Ohm)
                        double G = 1.0 / 1e-6;
                        if (u > 0) A[u-1][u-1] += G;
                        if (v > 0) A[v-1][v-1] += G;
                        if (u > 0 && v > 0) {
                            A[u-1][v-1] -= G;
                            A[v-1][u-1] -= G;
                        }
                    }
                }
            }

            // Populate independent voltage sources (M equations)
            for (int k = 0; k < M; ++k) {
                const auto& src = volt_sources[k];
                double value = src.value("value", 0.0);
                std::string p_id = src["pins"][0].value("nodeId", "0");
                std::string n_id = src["pins"][1].value("nodeId", "0");
                int p = node_map[p_id];
                int n = node_map[n_id];

                if (p > 0) {
                    A[p-1][N + k] += 1.0;
                    A[N + k][p-1] += 1.0;
                }
                if (n > 0) {
                    A[n-1][N + k] -= 1.0;
                    A[N + k][n-1] -= 1.0;
                }
                B[N + k] = value;
            }

            // Solve linear system
            std::vector<double> x;
            bool success = solve_system(A, B, x);

            if (!success) {
                res.status = 422;
                res.body = json{{"status", "error"}, {"message", "Matrix is singular (unstable circuit, floating nodes, or shorted sources)"}}.dump();
                return;
            }

            // Map solved voltages
            json node_voltages = json::object();
            node_voltages["0"] = 0.0; // Ground is always 0V
            for (int i = 1; i < num_nodes; ++i) {
                node_voltages[node_ids[i]] = x[i-1];
            }

            // Map solved currents
            json comp_currents = json::object();
            int v_src_idx = 0;
            for (const auto& comp : root["components"]) {
                std::string comp_id = comp.value("id", "");
                std::string type = comp.value("type", "");
                double value = comp.value("value", 0.0);

                if (comp.contains("pins") && comp["pins"].is_array() && comp["pins"].size() >= 2) {
                    std::string n1_id = comp["pins"][0].value("nodeId", "0");
                    std::string n2_id = comp["pins"][1].value("nodeId", "0");
                    double v1 = node_voltages[n1_id].get<double>();
                    double v2 = node_voltages[n2_id].get<double>();

                    if (type == "RESISTOR") {
                        comp_currents[comp_id] = (v1 - v2) / (value > 0 ? value : 1e-12);
                    } else if (type == "CAPACITOR") {
                        comp_currents[comp_id] = 0.0;
                    } else if (type == "INDUCTOR") {
                        comp_currents[comp_id] = (v1 - v2) / 1e-6;
                    } else if (type == "DC_VOLTAGE") {
                        comp_currents[comp_id] = x[N + v_src_idx];
                        v_src_idx++;
                    }
                }
            }

            res.body = json{
                {"status", "success"},
                {"voltages", node_voltages},
                {"currents", comp_currents}
            }.dump();

        } catch (const std::exception& e) {
            res.status = 500;
            res.body = json{{"status", "error"}, {"message", std::string("Internal exception: ") + e.what()}}.dump();
        }
    });

    std::cout << "C++ Simulation service listening on port 8081..." << std::endl;
    svr.listen("0.0.0.0", 8081);
    return 0;
}
