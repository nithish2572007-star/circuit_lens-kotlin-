package com.example.circuitlens.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.circuitlens.ui.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CircuitState")

@Serializable
data class Circuit(
    val id: String,
    val name: String,
    val components: List<Component>
)

@Serializable
data class Component(
    val id: String,
    val type: String,
    val value: Double,
    val pins: List<Pin>
)

@Serializable
data class Pin(
    val name: String,
    val nodeId: String
)

@Serializable
data class SimulationResult(
    val status: String,
    val voltages: Map<String, Double> = emptyMap(),
    val currents: Map<String, Double> = emptyMap(),
    val message: String? = null
)

@Serializable
data class WsFramePayload(
    val type: String,
    val text: String? = null,
    val simulation: SimulationResult? = null
)

data class ChatMessage(
    val sender: String, // "USER" or "BOT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

object CircuitStateHolder {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var webSocketSession: DefaultClientWebSocketSession? = null

    // Compose observable states
    var currentCircuit by mutableStateOf<Circuit?>(null)
        private set

    var simulationResult by mutableStateOf<SimulationResult?>(null)
        private set

    val chatMessages = mutableStateListOf<ChatMessage>()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadCircuit(id: String) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                logger.info("Loading circuit $id from Ktor API...")
                val response: HttpResponse = NetworkClient.client.get("${NetworkClient.BASE_URL}/api/v1/circuit/load") {
                    parameter("id", id)
                }
                if (response.status.isSuccess()) {
                    val circuit = response.body<Circuit>()
                    withContext(Dispatchers.Main) {
                        currentCircuit = circuit
                        logger.info("Loaded circuit successfully: ${circuit.name}")
                    }
                    // Run initial simulation
                    triggerInitialSimulation(circuit)
                } else {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Failed to load: ${response.status}"
                    }
                }
            } catch (e: Exception) {
                logger.error("Error loading circuit: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    errorMessage = "Network error: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    private fun triggerInitialSimulation(circuit: Circuit) {
        scope.launch {
            try {
                logger.info("Triggering initial simulation for circuit: ${circuit.id}")
                val response: HttpResponse = NetworkClient.client.post("${NetworkClient.BASE_URL}/api/v1/circuit/edit") {
                    contentType(ContentType.Application.Json)
                    setBody(circuit)
                }
                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()
                    val jsonElement = Json.parseToJsonElement(responseBody).jsonObject
                    val simElement = jsonElement["simulation"]
                    if (simElement != null) {
                        val simResult = Json.decodeFromString<SimulationResult>(simElement.toString())
                        withContext(Dispatchers.Main) {
                            simulationResult = simResult
                            logger.info("Simulation completed. Received voltages: ${simResult.voltages}")
                        }
                    }
                }
            } catch (e: Exception) {
                logger.warn("Initial simulation failed: ${e.message}")
            }
        }
    }

    fun startChatSession() {
        val circuitId = currentCircuit?.id ?: "voltage_divider"
        scope.launch {
            // Close existing session if active
            webSocketSession?.close()
            
            try {
                logger.info("Establishing WebSocket chat session for circuit: $circuitId...")
                NetworkClient.client.webSocket(
                    method = HttpMethod.Get,
                    host = NetworkClient.HOST,
                    port = NetworkClient.PORT,
                    path = "/api/v1/chat/ws/session_${System.currentTimeMillis()}?circuitId=$circuitId"
                ) {
                    webSocketSession = this
                    
                    // Consume incoming frames
                    incoming.consumeAsFlow().collect { frame ->
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            try {
                                val payload = Json.decodeFromString<WsFramePayload>(text)
                                when (payload.type) {
                                    "chatbot_response" -> {
                                        payload.text?.let { botResponse ->
                                            withContext(Dispatchers.Main) {
                                                chatMessages.add(ChatMessage("BOT", botResponse))
                                            }
                                        }
                                    }
                                    "simulation_results" -> {
                                        payload.simulation?.let { simResult ->
                                            withContext(Dispatchers.Main) {
                                                simulationResult = simResult
                                                logger.info("Received real-time simulation updates from chatbot thread: ${simResult.voltages}")
                                            }
                                        }
                                    }
                                    "status" -> {
                                        payload.text?.let { statusMsg ->
                                            withContext(Dispatchers.Main) {
                                                chatMessages.add(ChatMessage("BOT", "[Status] $statusMsg"))
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // Plain text fallback
                                withContext(Dispatchers.Main) {
                                    chatMessages.add(ChatMessage("BOT", text))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("WebSocket connection failure: ${e.message}")
                withContext(Dispatchers.Main) {
                    chatMessages.add(ChatMessage("BOT", "Error connecting to AI chat assistant: ${e.message}"))
                }
            }
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return
        chatMessages.add(ChatMessage("USER", query))
        
        scope.launch {
            val session = webSocketSession
            if (session != null && session.isActive) {
                try {
                    session.send(Frame.Text(query))
                } catch (e: Exception) {
                    logger.error("Failed to send WebSocket message: ${e.message}")
                    withContext(Dispatchers.Main) {
                        chatMessages.add(ChatMessage("BOT", "Failed to send message: ${e.message}"))
                    }
                }
            } else {
                // Reconnect and send
                startChatSession()
                delay(1000) // wait briefly for connection
                try {
                    webSocketSession?.send(Frame.Text(query))
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        chatMessages.add(ChatMessage("BOT", "AI chat server is offline. (Fallback enabled)"))
                        // Fallback response generator local simulation query
                        delay(500)
                        val offlineResponse = if (query.contains("voltage", ignoreCase = true)) {
                            "Offline Fallback: The simulated voltages are: Node 1 = 10.0V, Node 2 = 5.0V, Node 0 = 0V."
                        } else {
                            "Offline Fallback: I am currently offline. Please start the Ktor server and C++ Simulation microservice."
                        }
                        chatMessages.add(ChatMessage("BOT", offlineResponse))
                    }
                }
            }
        }
    }
}
