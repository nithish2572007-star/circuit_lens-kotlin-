package com.example.circuitlens.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.circuitlens.ui.components.CircuitLensBottomBar
import com.example.circuitlens.ui.navigation.Screen
import com.example.circuitlens.ui.screens.*
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.DarkBg

@Composable
fun CircuitLensApp() {
    // BUG-04 FIX: Implement a navigation backstack
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    val backStack = remember { mutableStateListOf<Screen>() }

    fun navigateTo(screen: Screen) {
        if (screen != currentScreen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }
    }

    // BUG-04 FIX: Handle system back button
    BackHandler(enabled = backStack.isNotEmpty()) {
        val previous = backStack.removeLastOrNull()
        if (previous != null) {
            currentScreen = previous
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (initialState == Screen.LOGIN && targetState == Screen.HOME) {
                                (slideInVertically(animationSpec = tween(500)) { height -> height } + fadeIn(animationSpec = tween(500))).togetherWith(
                                    fadeOut(animationSpec = tween(500))
                                )
                            } else if (initialState == Screen.PROFILE && targetState == Screen.HOME) {
                                (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300))).togetherWith(
                                    slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300))
                                )
                            } else {
                                (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300))).togetherWith(
                                    slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300))
                                )
                            }
                        },
                        label = "screenTransition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            Screen.LOGIN -> LoginScreen(onNavigate = { navigateTo(it) })
                            Screen.SIGNUP -> SignUpScreen(onNavigate = { navigateTo(it) })
                            Screen.HOME -> HomeScreen(onProfileClick = { navigateTo(Screen.PROFILE) }, onScanClick = { navigateTo(Screen.SCAN) })
                            Screen.SCAN -> ScanScreen(onProfileClick = { navigateTo(Screen.PROFILE) }, onNavigateToChat = { navigateTo(Screen.CHAT) })
                            Screen.CHAT -> ChatScreen(onProfileClick = { navigateTo(Screen.PROFILE) })
                            Screen.HISTORY -> HistoryScreen(onProfileClick = { navigateTo(Screen.PROFILE) })
                            Screen.PROFILE -> ProfileScreen(onBack = {
                                val previous = backStack.removeLastOrNull()
                                if (previous != null) currentScreen = previous
                                else currentScreen = Screen.HOME
                            })
                        }
                    }
                }

                // Show bottom bar only on core operational screens
                if (currentScreen in listOf(Screen.HOME, Screen.SCAN, Screen.CHAT, Screen.HISTORY)) {
                    CircuitLensBottomBar(
                        currentScreen = currentScreen,
                        onTabSelected = { navigateTo(it) }
                    )
                }
            }
        }
    }
}
