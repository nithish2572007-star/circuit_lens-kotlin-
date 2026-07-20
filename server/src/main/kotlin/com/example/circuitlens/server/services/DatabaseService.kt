package com.example.circuitlens.server.services

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseService {
    private val logger = LoggerFactory.getLogger(DatabaseService::class.java)
    private var dataSource: HikariDataSource? = null

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

    object UsersTable : Table("users") {
        val id = integer("id").autoIncrement()
        val email = varchar("email", 255).uniqueIndex()
        val passwordHash = varchar("password_hash", 255)
        val firstName = varchar("first_name", 100)
        val lastName = varchar("last_name", 100)
        override val primaryKey = PrimaryKey(id)
    }

    fun init() {
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/circuitlens"
        val dbUser = System.getenv("DB_USER") ?: "postgres"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "postgres"

        try {
            logger.info("Initializing HikariCP connection pool for PostgreSQL at $dbUrl...")
            val config = HikariConfig().apply {
                jdbcUrl = dbUrl
                username = dbUser
                password = dbPassword
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = 10
                minimumIdle = 2
                idleTimeout = 30000
                connectionTimeout = 5000
                leakDetectionThreshold = 2000
            }
            dataSource = HikariDataSource(config)
            Database.connect(dataSource!!)

            transaction {
                SchemaUtils.create(CircuitsTable, CircuitVersionsTable, ChatHistoryTable, UsersTable)
            }
            logger.info("Successfully initialized PostgreSQL database schema using HikariCP.")
        } catch (e: Exception) {
            logger.warn("Failed to connect to PostgreSQL. Falling back to in-memory H2 database. Error: ${e.message}")
            try {
                val config = HikariConfig().apply {
                    jdbcUrl = "jdbc:h2:mem:circuitlens;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
                    driverClassName = "org.h2.Driver"
                    maximumPoolSize = 5
                }
                dataSource = HikariDataSource(config)
                Database.connect(dataSource!!)

                transaction {
                    SchemaUtils.create(CircuitsTable, CircuitVersionsTable, ChatHistoryTable, UsersTable)
                }
                logger.info("Successfully initialized in-memory H2 database schema using HikariCP.")
            } catch (h2Ex: Exception) {
                logger.error("Failed to initialize fallback H2 database!", h2Ex)
            }
        }
    }

    fun close() {
        try {
            dataSource?.close()
            logger.info("Closed database connection pool successfully.")
        } catch (e: Exception) {
            logger.error("Failed to close database connection pool: ${e.message}", e)
        }
    }
}
