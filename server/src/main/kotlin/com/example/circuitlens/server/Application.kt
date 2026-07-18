package com.example.circuitlens.server

import com.example.circuitlens.server.routes.chatRoutes
import com.example.circuitlens.server.routes.circuitRoutes
import com.example.circuitlens.server.services.DatabaseService
import com.example.circuitlens.server.services.RedisCacheService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.time.Duration

fun main() {
    val port = (System.getenv("PORT") ?: "8080").toInt()
    println("Starting Ktor server on port $port...")
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    // 1. Initialize DB and Redis
    DatabaseService.init()
    RedisCacheService.init()

    // 2. Install WebSockets
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // 3. Install Content Negotiation
    install(ContentNegotiation) {
        json()
    }

    // 4. Configure Routing
    routing {
        circuitRoutes()
        chatRoutes()
    }
}
