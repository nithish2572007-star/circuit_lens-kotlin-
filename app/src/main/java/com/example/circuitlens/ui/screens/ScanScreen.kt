package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip

@Composable
fun ScanScreen(onProfileClick: () -> Unit, onNavigateToChat: () -> Unit) {
    // BUG-05 FIX: Track whether a circuit load has been triggered and navigate only when loaded
    var pendingNavigate by remember { mutableStateOf(false) }
    val isLoading = CircuitStateHolder.isLoading
    val errorMessage = CircuitStateHolder.errorMessage

    // Navigate to chat only once the circuit has actually finished loading
    LaunchedEffect(isLoading, pendingNavigate) {
        if (pendingNavigate && !isLoading && CircuitStateHolder.currentCircuit != null) {
            pendingNavigate = false
            onNavigateToChat()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader(onProfileClick = onProfileClick)
        Text("Capture or Upload", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Your Circuits", color = LimePrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Take a clear photo or upload an image of your circuit board or diagram.", color = TextGray, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))

        // BUG-12 FIX: Show loading indicator
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LimePrimary)
            }
        }

        // BUG-12 FIX: Show error message
        if (errorMessage != null && !isLoading) {
            Text(
                text = "Error: $errorMessage",
                color = Color(0xFFFF6B6B),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Large Option 1: Take a photo -> Loads Voltage Divider
        ScanOptionItem(
            title = "Take a photo (Voltage Divider)",
            subtitle = "Simulate mock live camera analysis",
            icon = Icons.Default.PhotoCamera,
            enabled = !isLoading,
            onClick = {
                CircuitStateHolder.loadCircuit("voltage_divider")
                pendingNavigate = true  // BUG-05 FIX: Navigate after load completes
            }
        )

        // Large Option 2: Choose from Gallery -> Loads RC Circuit
        Spacer(modifier = Modifier.height(16.dp))
        ScanOptionItem(
            title = "Choose from Gallery (RC Circuit)",
            subtitle = "Select a saved schematic diagram",
            icon = Icons.Default.Image,
            enabled = !isLoading,
            onClick = {
                CircuitStateHolder.loadCircuit("rc_circuit")
                pendingNavigate = true  // BUG-05 FIX: Navigate after load completes
            }
        )
    }
}

@Composable
fun ScanOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressedAlpha by androidx.compose.animation.core.animateFloatAsState(targetValue = if (isPressed) 1f else 0f)
    val contentColor by androidx.compose.animation.animateColorAsState(targetValue = if (isPressed) Color.Black else Color.White)
    val subtitleColor by androidx.compose.animation.animateColorAsState(targetValue = if (isPressed) Color.DarkGray else TextGray)

    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, LimePrimary), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.horizontalGradient(listOf(LimePrimary, LimeGradientEnd)), alpha = pressedAlpha)
        )
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = contentColor.copy(alpha = alpha), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = contentColor.copy(alpha = alpha), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(subtitle, color = subtitleColor.copy(alpha = alpha), fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = contentColor.copy(alpha = alpha))
        }
    }
}
