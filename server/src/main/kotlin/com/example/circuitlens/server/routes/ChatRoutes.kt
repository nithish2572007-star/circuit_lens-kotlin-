package com.example.circuitlens.server.routes

import com.example.circuitlens.server.services.DatabaseService
import com.example.circuitlens.server.services.LLMService
import com.example.circuitlens.server.services.RedisCacheService
import com.example.circuitlens.server.services.SimulationService
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ChatRoutes")

fun Route.chatRoutes() {
    webSocket("/api/v1/chat/ws/{sessionId}") {
        val sessionId = call.parameters["sessionId"] ?: "default_session"
        val circuitId = call.parameters["circuitId"]

        logger.info("New WebSocket connection. Session ID: $sessionId, Circuit ID: $circuitId")
        send(Frame.Text("Connected to CircuitLens Chatbot Server. Session: $sessionId"))

        try {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val userQuery = frame.readText().trim()
                    if (userQuery.isEmpty()) continue

                    logger.info("Received query: '$userQuery'")

                    // 1. Retrieve current circuit state from Redis
                    val circuitJson = circuitId?.let { RedisCacheService.getCircuit(it) }

                    // 2. Query the LLM
                    val responseText = LLMService.getChatbotResponse(circuitJson, userQuery)

                    // 3. Send back LLM Response
                    val llmFrame = buildJsonObject {
                        put("type", "chatbot_response")
                        put("text", responseText)
                    }.toString()
                    send(Frame.Text(llmFrame))

                    // 4. If query triggers simulation, also run simulation and push results
                    if (userQuery.contains("simulate", ignoreCase = true) || userQuery.contains("run simulation", ignoreCase = true)) {
                        if (circuitJson != null) {
                            send(Frame.Text(buildJsonObject { put("type", "status"); put("text", "Running simulation...") }.toString()))
                            val simResults = SimulationService.simulateCircuit(circuitJson)
                            val simFrame = buildJsonObject {
                                put("type", "simulation_results")
                                put("simulation", Json.parseToJsonElement(simResults))
                            }.toString()
                            send(Frame.Text(simFrame))
                        } else {
                            send(Frame.Text(buildJsonObject { put("type", "status"); put("text", "No active circuit loaded to simulate.") }.toString()))
                        }
                    }

                    // 5. Asynchronously persist chat history into PostgreSQL
                    launch(Dispatchers.IO) {
                        try {
                            transaction {
                                DatabaseService.ChatHistoryTable.insert {
                                    it[this.sessionId] = sessionId
                                    it[this.sender] = "USER"
                                    it[this.message] = userQuery
                                    it[this.createdAt] = System.currentTimeMillis()
                                }
                                DatabaseService.ChatHistoryTable.insert {
                                    it[this.sessionId] = sessionId
                                    it[this.sender] = "BOT"
                                    it[this.message] = responseText
                                    it[this.createdAt] = System.currentTimeMillis()
                                }
                            }
                        } catch (e: Exception) {
                            logger.warn("Failed to persist chat history to database: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn("WebSocket connection error: ${e.message}")
        } finally {
            logger.info("WebSocket connection closed for Session ID: $sessionId")
        }
    }
}
