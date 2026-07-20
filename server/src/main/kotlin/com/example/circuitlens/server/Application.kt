package com.example.circuitlens.server

import com.example.circuitlens.server.routes.authRoutes
import com.example.circuitlens.server.routes.chatRoutes
import com.example.circuitlens.server.routes.circuitRoutes
import com.example.circuitlens.server.services.DatabaseService
import com.example.circuitlens.server.services.RedisCacheService
import com.example.circuitlens.server.services.SimulationService
import com.example.circuitlens.server.services.LLMService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
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

    // BUG-11 FIX: Install CORS plugin for cross-origin requests
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
    }

    // 4. Configure Routing
    routing {
        circuitRoutes()
        chatRoutes()
        authRoutes()
    }

    // BUG-08/09 FIX: Close HttpClient singletons when the application shuts down
    environment.monitor.subscribe(ApplicationStopping) {
        SimulationService.close()
        LLMService.close()
    }
}
