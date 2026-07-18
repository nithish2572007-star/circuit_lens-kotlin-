package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState

@Composable
fun HistoryScreen(onProfileClick: () -> Unit) {
    val itemsList = listOf("CHAT 1", "CHAT 2", "CHAT 3", "CHAT 4", "CHAT 5")
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader(onProfileClick = onProfileClick)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(itemsList) { item ->
                val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val pressedAlpha by androidx.compose.animation.core.animateFloatAsState(targetValue = if (isPressed) 1f else 0f)
                val contentColor by androidx.compose.animation.animateColorAsState(targetValue = if (isPressed) Color.Black else Color.White)
                val subtitleColor by androidx.compose.animation.animateColorAsState(targetValue = if (isPressed) Color.DarkGray else TextGray)
                val iconBgColor by androidx.compose.animation.animateColorAsState(targetValue = if (isPressed) Color.Black.copy(alpha = 0.2f) else BorderGreen.copy(alpha = 0.3f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, LimePrimary), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBg)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { /* handle click */ }
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(LimePrimary, com.example.circuitlens.ui.theme.LimeGradientEnd)), alpha = pressedAlpha)
                    )
                    
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(44.dp)) {
                            Box(modifier = Modifier.fillMaxSize().background(iconBgColor, RoundedCornerShape(8.dp)))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item, color = contentColor, fontWeight = FontWeight.Bold)
                            Text("Date", color = subtitleColor, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Open Chat", tint = contentColor)
                    }
                }
            }
        }
    }
}
