package com.example.circuitlens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.components.CollapsibleSection
import com.example.circuitlens.ui.components.OverviewCard
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray

@Composable
fun ChatScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OverviewCard(modifier = Modifier.weight(1f), count = "2", label = "Components Detected", icon = Icons.Default.Memory)
            OverviewCard(modifier = Modifier.weight(1f), count = "3", label = "Connections Detected", icon = Icons.Default.DeviceHub)
            OverviewCard(modifier = Modifier.weight(1f), count = "1", label = "Error Detected", icon = Icons.Default.BugReport)
        }

        Spacer(modifier = Modifier.height(16.dp))
        CollapsibleSection(title = "DETECTED NETLIST", placeholder = "(Netlist here)")
        Spacer(modifier = Modifier.height(12.dp))
        CollapsibleSection(title = "ISSUES DETECTED (1)", placeholder = "(Issues here)")

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Query Box
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Ask CircuitLens...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.White) },
            trailingIcon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LimePrimary,
                unfocusedBorderColor = BorderGreen,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg
            )
        )
    }
}
