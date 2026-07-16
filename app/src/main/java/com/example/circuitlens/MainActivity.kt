package com.example.circuitlens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.circuitlens.ui.CircuitLensApp
import com.example.circuitlens.ui.theme.CircuitLensTheme // Keep your default theme import if you want to use it later

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Launching the main Compose UI
            CircuitLensApp()
        }
    }
}