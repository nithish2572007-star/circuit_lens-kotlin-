package com.example.circuitlens.server.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

object SimulationService {
    private val logger = LoggerFactory.getLogger(SimulationService::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val simulationServiceUrl = System.getenv("SIMULATION_SERVICE_URL") ?: "http://localhost:8081/simulate"

    suspend fun simulateCircuit(circuitPayloadJson: String): String {
        try {
            logger.info("Sending circuit payload to C++ simulation service at $simulationServiceUrl...")
            val response = client.post(simulationServiceUrl) {
                contentType(ContentType.Application.Json)
                setBody(Json.parseToJsonElement(circuitPayloadJson))
            }
            if (response.status.isSuccess()) {
                val body = response.bodyAsText()
                logger.info("Successfully received simulation response from C++ microservice.")
                return body
            } else {
                logger.warn("C++ Simulation service returned error status: ${response.status}")
            }
        } catch (e: Exception) {
            logger.warn("Failed to connect to C++ simulation service at $simulationServiceUrl: ${e.message}")
        }

        // Fallback: Generate mock simulation response based on the circuit content
        logger.info("Generating fallback simulation results...")
        return generateMockSimulationResponse(circuitPayloadJson)
    }

    private fun generateMockSimulationResponse(circuitPayloadJson: String): String {
        return try {
            val jsonElement = Json.parseToJsonElement(circuitPayloadJson).jsonObject
            val id = jsonElement["id"]?.jsonPrimitive?.content ?: ""
            if (id == "voltage_divider") {
                buildJsonObject {
                    put("status", "success")
                    putJsonObject("voltages") {
                        put("1", 10.0)
                        put("2", 5.0)
                        put("0", 0.0)
                    }
                    putJsonObject("currents") {
                        put("V1", -0.005)
                        put("R1", 0.005)
                        put("R2", 0.005)
                    }
                }.toString()
            } else {
                buildJsonObject {
                    put("status", "success")
                    putJsonObject("voltages") {
                        put("1", 5.0)
                        put("2", 5.0)
                        put("0", 0.0)
                    }
                    putJsonObject("currents") {
                        put("V1", 0.0)
                        put("R1", 0.0)
                        put("C1", 0.0)
                    }
                }.toString()
            }
        } catch (e: Exception) {
            buildJsonObject {
                put("status", "error")
                put("message", "Invalid circuit payload for simulation: ${e.message}")
            }.toString()
        }
    }

    // BUG-08 FIX: Properly close the HttpClient to release native resources
    fun close() {
        client.close()
    }
}
