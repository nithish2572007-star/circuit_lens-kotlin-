package com.example.circuitlens.server.routes

import com.example.circuitlens.server.models.Circuit
import com.example.circuitlens.server.services.DatabaseService
import com.example.circuitlens.server.services.RedisCacheService
import com.example.circuitlens.server.services.SimulationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("CircuitRoutes")

fun Route.circuitRoutes() {
    route("/api/v1/circuit") {
        get("/load") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
                return@get
            }

            // 1. Check Redis Cache
            val cachedJson = RedisCacheService.getCircuit(id)
            if (cachedJson != null) {
                call.respondText(cachedJson, ContentType.Application.Json)
                return@get
            }

            // 2. Check Database
            var dbJson: String? = null
            transaction {
                DatabaseService.CircuitsTable.selectAll().where { DatabaseService.CircuitsTable.id eq id }
                    .singleOrNull()?.let {
                        dbJson = it[DatabaseService.CircuitsTable.payload]
                    }
            }

            if (dbJson != null) {
                RedisCacheService.setCircuit(id, dbJson!!)
                call.respondText(dbJson!!, ContentType.Application.Json)
                return@get
            }

            // 3. Check Mocks in Resources
            val mockStream: InputStream? = object {}.javaClass.classLoader.getResourceAsStream("mocks/$id.json")
            if (mockStream != null) {
                val mockContent = mockStream.bufferedReader().use { it.readText() }
                // Save to DB and Cache
                transaction {
                    DatabaseService.CircuitsTable.insertIgnore {
                        it[DatabaseService.CircuitsTable.id] = id
                        it[DatabaseService.CircuitsTable.name] = id.replace("_", " ").replaceFirstChar { c -> c.titlecase() }
                        it[DatabaseService.CircuitsTable.payload] = mockContent
                    }
                }
                RedisCacheService.setCircuit(id, mockContent)
                call.respondText(mockContent, ContentType.Application.Json)
                return@get
            }

            call.respond(HttpStatusCode.NotFound, "Circuit with id '$id' not found")
        }

        post("/edit") {
            val circuit = call.receive<Circuit>()
            val circuitJson = Json.encodeToString(Circuit.serializer(), circuit)

            // 1. Update Database
            var lastVersion = 0
            transaction {
                val exists = DatabaseService.CircuitsTable.selectAll().where { DatabaseService.CircuitsTable.id eq circuit.id }.any()
                if (exists) {
                    DatabaseService.CircuitsTable.update({ DatabaseService.CircuitsTable.id eq circuit.id }) {
                        it[payload] = circuitJson
                        it[name] = circuit.name
                    }
                } else {
                    DatabaseService.CircuitsTable.insert {
                        it[id] = circuit.id
                        it[name] = circuit.name
                        it[payload] = circuitJson
                    }
                }

                // 2. Determine new version
                val currentVersion = DatabaseService.CircuitVersionsTable
                    .selectAll().where { DatabaseService.CircuitVersionsTable.circuitId eq circuit.id }
                    .orderBy(DatabaseService.CircuitVersionsTable.version, SortOrder.DESC)
                    .limit(1)
                    .singleOrNull()?.get(DatabaseService.CircuitVersionsTable.version) ?: 0

                lastVersion = currentVersion
                val newVersion = currentVersion + 1

                // 3. Save historical version
                DatabaseService.CircuitVersionsTable.insert {
                    it[circuitId] = circuit.id
                    it[version] = newVersion
                    it[payload] = circuitJson
                    it[createdAt] = System.currentTimeMillis()
                }
            }

            // 4. Cache in Redis
            RedisCacheService.setCircuit(circuit.id, circuitJson)

            // 5. Run Simulation directly
            val simResultsJson = SimulationService.simulateCircuit(circuitJson)

            // 6. Return response containing updated circuit and simulation results
            val responseJson = buildJsonObject {
                put("status", "success")
                put("version", lastVersion + 1)
                put("circuitId", circuit.id)
                put("simulation", Json.parseToJsonElement(simResultsJson))
            }.toString()

            call.respondText(responseJson, ContentType.Application.Json)
        }

        get("/history") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
                return@get
            }

            val versions = mutableListOf<kotlinx.serialization.json.JsonObject>()
            transaction {
                DatabaseService.CircuitVersionsTable.selectAll()
                    .where { DatabaseService.CircuitVersionsTable.circuitId eq id }
                    .orderBy(DatabaseService.CircuitVersionsTable.version, SortOrder.ASC)
                    .forEach {
                        versions.add(buildJsonObject {
                            put("id", it[DatabaseService.CircuitVersionsTable.id])
                            put("circuitId", it[DatabaseService.CircuitVersionsTable.circuitId])
                            put("version", it[DatabaseService.CircuitVersionsTable.version])
                            put("payload", Json.parseToJsonElement(it[DatabaseService.CircuitVersionsTable.payload]))
                            put("createdAt", it[DatabaseService.CircuitVersionsTable.createdAt])
                        })
                    }
            }

            call.respondText(versions.toString(), ContentType.Application.Json)
        }
    }
}

