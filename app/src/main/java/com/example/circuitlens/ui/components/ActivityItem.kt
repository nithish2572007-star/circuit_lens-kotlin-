package com.example.circuitlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimeGradientEnd
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray

@Composable
fun ActivityItem(title: String, subtitle: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressedAlpha by animateFloatAsState(targetValue = if (isPressed) 1f else 0f)
    val contentColor by animateColorAsState(targetValue = if (isPressed) Color.Black else Color.White)
    val subtitleColor by animateColorAsState(targetValue = if (isPressed) Color.DarkGray else TextGray)
    val iconBgColor by animateColorAsState(targetValue = if (isPressed) Color.Black.copy(alpha = 0.2f) else BorderGreen.copy(alpha = 0.3f))

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
        // Gradient overlay for press animation
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.horizontalGradient(listOf(LimePrimary, LimeGradientEnd)).apply { }, alpha = pressedAlpha)
        )
        
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).background(iconBgColor, RoundedCornerShape(8.dp)))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = contentColor, fontWeight = FontWeight.Bold)
                Text(subtitle, color = subtitleColor, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "View", tint = contentColor)
        }
    }
}
