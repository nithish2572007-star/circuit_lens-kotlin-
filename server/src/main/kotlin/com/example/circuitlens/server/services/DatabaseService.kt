package com.example.circuitlens.server.services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseService {
    private val logger = LoggerFactory.getLogger(DatabaseService::class.java)

    object CircuitsTable : Table("circuits") {
        val id = varchar("id", 100)
        val name = varchar("name", 255)
        val payload = text("payload")
        override val primaryKey = PrimaryKey(id)
    }

    object CircuitVersionsTable : Table("circuit_versions") {
        val id = integer("id").autoIncrement()
        val circuitId = varchar("circuit_id", 100)
        val version = integer("version")
        val payload = text("payload")
        val createdAt = long("created_at")
        override val primaryKey = PrimaryKey(id)
    }

    object ChatHistoryTable : Table("chat_history") {
        val id = integer("id").autoIncrement()
        val sessionId = varchar("session_id", 100)
        val sender = varchar("sender", 50) // USER, BOT
        val message = text("message")
        val createdAt = long("created_at")
        override val primaryKey = PrimaryKey(id)
    }

    fun init() {
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/circuitlens"
        val dbUser = System.getenv("DB_USER") ?: "postgres"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "postgres"

        try {
            logger.info("Connecting to PostgreSQL at $dbUrl...")
            Database.connect(dbUrl, driver = "org.postgresql.Driver", user = dbUser, password = dbPassword)
            transaction {
                SchemaUtils.create(CircuitsTable, CircuitVersionsTable, ChatHistoryTable)
            }
            logger.info("Successfully initialized PostgreSQL database schema.")
        } catch (e: Exception) {
            logger.warn("Failed to connect to PostgreSQL ($dbUrl). Falling back to in-memory H2 database. Error: ${e.message}")
            try {
                Database.connect("jdbc:h2:mem:circuitlens;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", driver = "org.h2.Driver")
                transaction {
                    SchemaUtils.create(CircuitsTable, CircuitVersionsTable, ChatHistoryTable)
                }
                logger.info("Successfully initialized in-memory H2 database schema.")
            } catch (h2Ex: Exception) {
                logger.error("Failed to initialize fallback H2 database!", h2Ex)
            }
        }
    }
}
