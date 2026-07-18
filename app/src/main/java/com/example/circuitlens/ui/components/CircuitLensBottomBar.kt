package com.example.circuitlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.navigation.Screen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray

class CurvedBottomNavShape(
    private val xOffset: Float,
    private val bulgeRadius: Float = 90f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            val width = size.width
            val height = size.height
            val curveWidth = bulgeRadius * 2.5f

            moveTo(0f, 0f)

            // Left side flat part
            val startCurveX = xOffset - curveWidth / 2
            lineTo(startCurveX, 0f)

            // Smooth curve down (dip)
            cubicTo(
                xOffset - bulgeRadius, 0f,
                xOffset - bulgeRadius, bulgeRadius * 0.8f,
                xOffset, bulgeRadius * 0.8f
            )
            cubicTo(
                xOffset + bulgeRadius, bulgeRadius * 0.8f,
                xOffset + bulgeRadius, 0f,
                xOffset + curveWidth / 2, 0f
            )

            lineTo(width, 0f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        })
    }
}

@Composable
fun CircuitLensBottomBar(currentScreen: Screen, onTabSelected: (Screen) -> Unit) {
    var selectedX by remember { mutableFloatStateOf(0f) }
    val animatedX by animateFloatAsState(
        targetValue = selectedX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "bulgeX"
    )

    val screens = listOf(
        Triple(Screen.HOME, Icons.Default.Home, "Home"),
        Triple(Screen.SCAN, Icons.Default.QrCodeScanner, "Scan"),
        Triple(Screen.CHAT, Icons.Default.ChatBubbleOutline, "Chat"),
        Triple(Screen.HISTORY, Icons.Default.AccessTime, "History")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                color = CardBg,
                shape = CurvedBottomNavShape(animatedX)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            screens.forEach { (screen, icon, label) ->
                val isActive = currentScreen == screen
                BottomNavItem(
                    screen = screen,
                    icon = icon,
                    label = label,
                    isActive = isActive,
                    onClick = onTabSelected,
                    onPositioned = { x -> if (isActive) selectedX = x }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    screen: Screen,
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: (Screen) -> Unit,
    onPositioned: (Float) -> Unit
) {
    val colorAnimationSpec = tween<Color>(durationMillis = 300)

    val iconColor by animateColorAsState(
        targetValue = if (isActive) Color.Black else TextGray,
        animationSpec = colorAnimationSpec,
        label = "iconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isActive) LimePrimary else Color.Transparent,
        animationSpec = colorAnimationSpec,
        label = "textColor"
    )
    
    val iconOffsetY by animateFloatAsState(
        targetValue = if (isActive) -16f else 0f,
        label = "iconOffsetY"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .onGloballyPositioned { coords ->
                onPositioned(coords.positionInParent().x + coords.size.width / 2f)
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick(screen) }
            .width(72.dp)
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .offset(y = iconOffsetY.dp)
                .clip(CircleShape)
                .background(if (isActive) LimePrimary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        if (isActive) {
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp).offset(y = (-4).dp)
            )
        } else {
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}
