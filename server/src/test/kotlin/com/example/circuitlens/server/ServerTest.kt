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
}
