package com.example.circuitlens.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.navigation.Screen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray

@Composable
fun CircuitLensBottomBar(currentScreen: Screen, onTabSelected: (Screen) -> Unit) {
    Surface(
        color = CardBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                screen = Screen.HOME,
                icon = Icons.Default.Home,
                label = "Home",
                isActive = currentScreen == Screen.HOME,
                onClick = onTabSelected
            )
            BottomNavItem(
                screen = Screen.SCAN,
                icon = Icons.Default.QrCodeScanner,
                label = "Scan",
                isActive = currentScreen == Screen.SCAN,
                onClick = onTabSelected
            )
            BottomNavItem(
                screen = Screen.CHAT,
                icon = Icons.Default.ChatBubbleOutline,
                label = "Chat",
                isActive = currentScreen == Screen.CHAT,
                onClick = onTabSelected
            )
            BottomNavItem(
                screen = Screen.HISTORY,
                icon = Icons.Default.AccessTime,
                label = "History",
                isActive = currentScreen == Screen.HISTORY,
                onClick = onTabSelected
            )
        }
    }
}

@Composable
fun BottomNavItem(
    screen: Screen,
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: (Screen) -> Unit
) {
    val activeColor by animateColorAsState(if (isActive) Color.Black else TextGray, label = "")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick(screen) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 36.dp)
                .background(
                    color = if (isActive) LimePrimary else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = activeColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) LimePrimary else TextGray,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
