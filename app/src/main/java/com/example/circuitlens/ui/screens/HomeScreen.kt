package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.ActivityItem
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.components.OverviewCard
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.*

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip

@Composable
fun HomeScreen(onProfileClick: () -> Unit, onScanClick: () -> Unit) {
    val activeCircuit = CircuitStateHolder.currentCircuit
    val simulationResult = CircuitStateHolder.simulationResult
    
    val analyzedCount = if (activeCircuit != null) "1" else "0"
    val errorsCount = if (simulationResult?.status == "error") "1" else "0"

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        CircuitHeader(onProfileClick = onProfileClick)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Welcome back,", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Medium)
            Text("Abc Xyz", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            if (activeCircuit != null) {
                Text("Active: ${activeCircuit.name}", color = LimePrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
            } else {
                Text("Let's analyze and perfect your circuits", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // Overview Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, LimePrimary), RoundedCornerShape(16.dp))
                    .background(DarkBg)
                    .padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Overview", color = Color.White, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Today", color = Color.White, fontSize = 14.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OverviewCard(modifier = Modifier.weight(1f), count = analyzedCount, label = "Circuits Loaded", icon = Icons.Default.Settings)
                        OverviewCard(modifier = Modifier.weight(1f), count = if (activeCircuit != null && errorsCount == "0") "1" else "0", label = "Healthy Circuits", icon = Icons.Default.CheckCircle)
                        OverviewCard(modifier = Modifier.weight(1f), count = errorsCount, label = "Errors Detected", icon = Icons.Default.Warning)
                    }
                }
            }

            // Action Button Card
            Spacer(modifier = Modifier.height(16.dp))
            val scanInteractionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            val isScanPressed by scanInteractionSource.collectIsPressedAsState()
            
            val scanPressedAlpha by androidx.compose.animation.core.animateFloatAsState(targetValue = if (isScanPressed) 1f else 0f)
            val scanContentColor by androidx.compose.animation.animateColorAsState(targetValue = if (isScanPressed) Color.Black else Color.White)
            val scanSubtitleColor by androidx.compose.animation.animateColorAsState(targetValue = if (isScanPressed) Color.DarkGray else TextGray)
            val scanIconBgColor by androidx.compose.animation.animateColorAsState(targetValue = if (isScanPressed) Color.Black.copy(alpha = 0.2f) else DarkBg)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBg)
                    .clickable(
                        interactionSource = scanInteractionSource,
                        indication = null,
                        onClick = onScanClick
                    )
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(LimePrimary, LimeGradientEnd)), alpha = scanPressedAlpha)
                )
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp).background(scanIconBgColor, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Scan", tint = scanContentColor)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Scan a circuit", color = scanContentColor, fontWeight = FontWeight.Bold)
                        Text("Capture or upload a photo to analyze", color = scanSubtitleColor, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = scanContentColor)
                }
            }

            // Recent Activity Section
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Recent Activity", color = Color.White, fontWeight = FontWeight.Bold)
                Text("View all", color = LimePrimary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ActivityItem(activeCircuit?.name ?: "No Circuit Loaded", if (activeCircuit != null) "Active session" else "Tap scan below to start")
        }
    }
}
