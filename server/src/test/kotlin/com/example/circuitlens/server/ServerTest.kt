package com.example.circuitlens.server

import com.example.circuitlens.server.models.Circuit
import com.example.circuitlens.server.models.Component
import com.example.circuitlens.server.models.Pin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServerTest {

    @Test
    fun testLoadCircuitMock() = testApplication {
        application {
            module()
        }
        val response = client.get("/api/v1/circuit/load?id=voltage_divider")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("voltage_divider"))
        assertTrue(body.contains("Simple Voltage Divider"))
    }

    @Test
    fun testEditCircuit() = testApplication {
        application {
            module()
        }
        val clientWithJson = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val testCircuit = Circuit(
            id = "test_circuit",
            name = "Test Circuit",
            components = listOf(
                Component(
                    id = "R1",
                    type = "RESISTOR",
                    value = 500.0,
                    pins = listOf(
                        Pin("p1", "1"),
                        Pin("p2", "0")
                    )
                )
            )
        )
        val response = clientWithJson.post("/api/v1/circuit/edit") {
            contentType(ContentType.Application.Json)
            setBody(testCircuit)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        assertEquals("success", json["status"]?.jsonPrimitive?.content)
        assertEquals("test_circuit", json["circuitId"]?.jsonPrimitive?.content)
        assertNotNull(json["simulation"])
    }

    @Test
    fun testHistoryCircuit() = testApplication {
        application {
            module()
        }
        val clientWithJson = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val testCircuit = Circuit(
            id = "test_history",
            name = "Test History",
            components = listOf(
                Component("R1", "RESISTOR", 500.0, listOf(Pin("p1", "1"), Pin("p2", "0")))
            )
        )
        // Edit once to create version 1
        clientWithJson.post("/api/v1/circuit/edit") {
            contentType(ContentType.Application.Json)
            setBody(testCircuit)
        }
        // Edit twice to create version 2
        val updatedCircuit = testCircuit.copy(name = "Test History Updated")
        clientWithJson.post("/api/v1/circuit/edit") {
            contentType(ContentType.Application.Json)
            setBody(updatedCircuit)
        }

        // Query history
        val response = clientWithJson.get("/api/v1/circuit/history?id=test_history")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("test_history"))
        assertTrue(body.contains("Test History"))
        assertTrue(body.contains("Test History Updated"))
    }

    @Test
    fun testAuthenticationFlow() = testApplication {
        application {
            module()
        }
        val clientWithJson = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // 1. Register a new user
        val signupRequest = com.example.circuitlens.server.routes.SignupRequest(
            email = "user@test.com",
            password = "securePassword123",
            firstName = "John",
            lastName = "Doe"
        )
        val signupRes = clientWithJson.post("/api/v1/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(signupRequest)
        }
        assertEquals(HttpStatusCode.Created, signupRes.status)
        val signupBody = signupRes.bodyAsText()
        assertTrue(signupBody.contains("success"))
        assertTrue(signupBody.contains("user@test.com"))

        // 2. Register same user should fail (conflict)
        val signupDupRes = clientWithJson.post("/api/v1/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(signupRequest)
        }
        assertEquals(HttpStatusCode.Conflict, signupDupRes.status)

        // 3. Login successfully
        val loginRequest = com.example.circuitlens.server.routes.LoginRequest(
            email = "user@test.com",
            password = "securePassword123"
        )
        val loginRes = clientWithJson.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }
        assertEquals(HttpStatusCode.OK, loginRes.status)
        val loginBody = loginRes.bodyAsText()
        assertTrue(loginBody.contains("success"))
        assertTrue(loginBody.contains("John"))

        // 4. Login failed (incorrect password)
        val badLoginRequest = loginRequest.copy(password = "wrongPassword")
        val badLoginRes = clientWithJson.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(badLoginRequest)
        }
        assertEquals(HttpStatusCode.Unauthorized, badLoginRes.status)
    }
}

