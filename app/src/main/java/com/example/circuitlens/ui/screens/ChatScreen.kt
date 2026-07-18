package com.example.circuitlens.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.components.OverviewCard
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(onProfileClick: () -> Unit) {
    var queryText by remember { mutableStateOf("") }
    var netlistExpanded by remember { mutableStateOf(false) }
    var telemetryExpanded by remember { mutableStateOf(false) }
    val chatMessages = CircuitStateHolder.chatMessages
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 1. Establish real-time WebSocket session when screen mounts
    LaunchedEffect(Unit) {
        CircuitStateHolder.startChatSession()
    }

    // Auto-scroll to the bottom when new chat messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val activeCircuit = CircuitStateHolder.currentCircuit
    val simulationResult = CircuitStateHolder.simulationResult

    val componentsCount = activeCircuit?.components?.size?.toString() ?: "0"
    val connectionsCount = activeCircuit?.components?.flatMap { it.pins }?.distinctBy { it.nodeId }?.size?.toString() ?: "0"
    val errorsCount = if (simulationResult?.status == "error") "1" else "0"

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader(onProfileClick = onProfileClick)

        // Statistics Overview Cards
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OverviewCard(modifier = Modifier.weight(1f), count = componentsCount, label = "Components Detected", icon = Icons.Default.Memory)
            OverviewCard(modifier = Modifier.weight(1f), count = connectionsCount, label = "Connections Detected", icon = Icons.Default.DeviceHub)
            OverviewCard(modifier = Modifier.weight(1f), count = errorsCount, label = "Errors Detected", icon = Icons.Default.BugReport)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Collapsible Section: DETECTED NETLIST
        val netlistPlaceholder = activeCircuit?.let { circuit ->
            circuit.components.joinToString("\n") { comp ->
                val pinsText = comp.pins.joinToString(" ") { it.nodeId }
                "${comp.id} $pinsText ${comp.value}"
            }
        } ?: "No active circuit loaded. Use 'Scan' to initialize."
        
        InteractiveCollapsibleSection(
            title = "DETECTED NETLIST",
            content = netlistPlaceholder,
            isExpanded = netlistExpanded,
            onToggle = { netlistExpanded = !netlistExpanded }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Collapsible Section: SIMULATION TELEMETRY
        val telemetryPlaceholder = if (simulationResult != null && simulationResult.status == "success") {
            val vText = simulationResult.voltages.entries.joinToString("\n") { "Node ${it.key}: ${String.format("%.2f", it.value)} V" }
            val cText = simulationResult.currents.entries.joinToString("\n") { "Branch ${it.key}: ${String.format("%.3f", it.value * 1000)} mA" }
            "Voltages:\n$vText\n\nCurrents:\n$cText"
        } else if (simulationResult != null && simulationResult.status == "error") {
            "Simulation Error: ${simulationResult.message}"
        } else {
            "No active simulation. Ask Chatbot to 'simulate' the circuit."
        }

        InteractiveCollapsibleSection(
            title = "SIMULATION TELEMETRY",
            content = telemetryPlaceholder,
            isExpanded = telemetryExpanded,
            onToggle = { telemetryExpanded = !telemetryExpanded }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Message History List
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ask CircuitLens to analyze node voltages, find wiring defects, or verify parameters.",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(chatMessages) { msg ->
                    ChatBubble(msg)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Query Input Box
        OutlinedTextField(
            value = queryText,
            onValueChange = { queryText = it },
            placeholder = { Text("Ask CircuitLens...", color = TextGray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color.White,
                    modifier = Modifier.clickable { /* Voice trigger not implemented */ }
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (queryText.isNotBlank()) LimePrimary else Color.White,
                    modifier = Modifier.clickable {
                        if (queryText.isNotBlank()) {
                            CircuitStateHolder.sendMessage(queryText)
                            queryText = ""
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LimePrimary,
                unfocusedBorderColor = BorderGreen,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun InteractiveCollapsibleSection(
    title: String,
    content: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BorderGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, LimePrimary), RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = Color.White
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(CardBg.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(content, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ChatBubble(message: com.example.circuitlens.ui.state.ChatMessage) {
    val isUser = message.sender == "USER"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bg = if (isUser) LimePrimary else CardBg
    val textColor = if (isUser) Color.Black else Color.White
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bg, shape)
                .border(BorderStroke(1.dp, if (isUser) Color.Transparent else BorderGreen), shape)
                .padding(12.dp)
        ) {
            Text(text = message.text, color = textColor, fontSize = 14.sp)
        }
    }
}
