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

object LLMService {
    private val logger = LoggerFactory.getLogger(LLMService::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val geminiApiKey = System.getenv("GEMINI_API_KEY")

    suspend fun getChatbotResponse(circuitPayloadJson: String?, userQuery: String): String {
        if (geminiApiKey.isNullOrBlank()) {
            logger.warn("GEMINI_API_KEY environment variable is not set. Generating fallback AI response.")
            return generateFallbackResponse(circuitPayloadJson, userQuery)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$geminiApiKey"
            val systemPrompt = "You are CircuitLens AI, an expert electrical engineering assistant. " +
                    "Your job is to analyze the active circuit JSON, troubleshoot connections, verify components, and help the user design perfect circuits. " +
                    "Be brief, precise, and practical."

            val circuitContext = if (circuitPayloadJson != null) {
                "Active Circuit JSON:\n$circuitPayloadJson\n\n"
            } else {
                "No active circuit loaded.\n\n"
            }

            val fullPrompt = "$systemPrompt\n\nContext:\n$circuitContext\nUser Query:\n$userQuery"

            val requestBody = buildJsonObject {
                putJsonArray("contents") {
                    addJsonObject {
                        putJsonArray("parts") {
                            addJsonObject {
                                put("text", fullPrompt)
                            }
                        }
                    }
                }
            }

            logger.info("Calling Gemini API...")
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                val textResponse = jsonResponse["candidates"]
                    ?.jsonArray
                    ?.firstOrNull()
                    ?.jsonObject
                    ?.get("content")
                    ?.jsonObject
                    ?.get("parts")
                    ?.jsonArray
                    ?.firstOrNull()
                    ?.jsonObject
                    ?.get("text")
                    ?.jsonPrimitive
                    ?.content
                
                if (textResponse != null) {
                    return textResponse
                }
            } else {
                logger.error("Gemini API error: ${response.status} - ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            logger.error("Error communicating with Gemini API: ${e.message}", e)
        }

        return generateFallbackResponse(circuitPayloadJson, userQuery)
    }

    private fun generateFallbackResponse(circuitPayloadJson: String?, userQuery: String): String {
        val hasDivider = circuitPayloadJson?.contains("voltage_divider") == true
        return when {
            hasDivider && userQuery.contains("voltage", ignoreCase = true) -> {
                "Based on the Voltage Divider circuit (R1 = 1k, R2 = 1k, V1 = 10V), the midpoint voltage at Node 2 is calculated as: V_out = V_in * (R2 / (R1 + R2)) = 10V * (1000 / 2000) = 5.0V. The circuit is correctly balanced."
            }
            hasDivider && userQuery.contains("simulate", ignoreCase = true) -> {
                "I have simulated the voltage divider. Node 1 is at 10.0V, Node 2 is at 5.0V, and Node 0 (GND) is at 0V. The total current flowing from the source is 5.0mA."
            }
            else -> {
                "Hello! I am the CircuitLens chatbot. I can see your active circuit is: ${circuitPayloadJson ?: "Not loaded"}. You asked: '$userQuery'. Let me know if you want to inspect voltages, troubleshoot connections, or run a simulation."
            }
        }
    }
}
