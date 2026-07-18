package com.example.circuitlens.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.circuitlens.ui.components.CircuitLensBottomBar
import com.example.circuitlens.ui.navigation.Screen
import com.example.circuitlens.ui.screens.*
import com.example.circuitlens.ui.theme.DarkBg

@Composable
fun CircuitLensApp() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                        },
                        label = "screenTransition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            Screen.LOGIN -> LoginScreen(onNavigate = { currentScreen = it })
                            Screen.SIGNUP -> SignUpScreen(onNavigate = { currentScreen = it })
                            Screen.HOME -> HomeScreen(onProfileClick = { currentScreen = Screen.PROFILE })
                            Screen.SCAN -> ScanScreen(onProfileClick = { currentScreen = Screen.PROFILE })
                            Screen.CHAT -> ChatScreen(onProfileClick = { currentScreen = Screen.PROFILE })
                            Screen.HISTORY -> HistoryScreen(onProfileClick = { currentScreen = Screen.PROFILE })
                            Screen.PROFILE -> ProfileScreen(onBack = { currentScreen = Screen.HOME })
                        }
                    }
                }

                // Show bottom bar only on core operational screens
                if (currentScreen in listOf(Screen.HOME, Screen.SCAN, Screen.CHAT, Screen.HISTORY)) {
                    CircuitLensBottomBar(
                        currentScreen = currentScreen,
                        onTabSelected = { currentScreen = it }
                    )
                }
            }
        }
    }
}
