package com.example.circuitlens.ui.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(WebSockets)
    }

    const val HOST = "10.0.2.2"
    const val PORT = 8080
    const val BASE_URL = "http://$HOST:$PORT"
    const val WS_URL = "ws://$HOST:$PORT"
}
