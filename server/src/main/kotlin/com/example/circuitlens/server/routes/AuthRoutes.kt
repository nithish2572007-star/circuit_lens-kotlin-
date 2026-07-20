package com.example.circuitlens.server.routes

import com.example.circuitlens.server.services.DatabaseService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AuthRoutes")

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignupRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class UserProfile(
    val email: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class AuthResponse(
    val status: String,
    val message: String,
    val user: UserProfile? = null
)

fun Route.authRoutes() {
    route("/api/v1/auth") {
        post("/signup") {
            try {
                val req = call.receive<SignupRequest>()
                if (req.email.isBlank() || req.password.isBlank() || req.firstName.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, AuthResponse("error", "Email, password, and first name are required"))
                    return@post
                }

                val emailLower = req.email.lowercase().trim()
                val hashedPassword = hashPassword(req.password)

                // BUG-02 FIX: Single transaction with insertIgnore to avoid TOCTOU race condition
                var inserted = false
                try {
                    transaction {
                        val result = DatabaseService.UsersTable.insertIgnore {
                            it[email] = emailLower
                            it[passwordHash] = hashedPassword
                            it[firstName] = req.firstName
                            it[lastName] = req.lastName
                        }
                        inserted = result.insertedCount > 0
                    }
                } catch (e: Exception) {
                    // Unique constraint violation — user already exists
                    logger.warn("Signup constraint violation for $emailLower: ${e.message}")
                    inserted = false
                }

                if (inserted) {
                    logger.info("Successfully registered user: $emailLower")
                    call.respond(HttpStatusCode.Created, AuthResponse("success", "Registration successful", UserProfile(emailLower, req.firstName, req.lastName)))
                } else {
                    call.respond(HttpStatusCode.Conflict, AuthResponse("error", "User with this email already exists"))
                }
            } catch (e: Exception) {
                logger.error("Signup error: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, AuthResponse("error", "Internal server error: ${e.message}"))
            }
        }

        post("/login") {
            try {
                val req = call.receive<LoginRequest>()
                if (req.email.isBlank() || req.password.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, AuthResponse("error", "Email and password are required"))
                    return@post
                }

                val emailLower = req.email.lowercase().trim()
                val hashedPassword = hashPassword(req.password)

                var matchedUser: UserProfile? = null
                transaction {
                    DatabaseService.UsersTable.selectAll()
                        .where { (DatabaseService.UsersTable.email eq emailLower) and (DatabaseService.UsersTable.passwordHash eq hashedPassword) }
                        .singleOrNull()?.let {
                            matchedUser = UserProfile(
                                email = it[DatabaseService.UsersTable.email],
                                firstName = it[DatabaseService.UsersTable.firstName],
                                lastName = it[DatabaseService.UsersTable.lastName]
                            )
                        }
                }

                if (matchedUser != null) {
                    logger.info("Successfully logged in user: $emailLower")
                    call.respond(HttpStatusCode.OK, AuthResponse("success", "Login successful", matchedUser))
                } else {
                    logger.warn("Failed login attempt for: $emailLower")
                    call.respond(HttpStatusCode.Unauthorized, AuthResponse("error", "Invalid email or password"))
                }
            } catch (e: Exception) {
                logger.error("Login error: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, AuthResponse("error", "Internal server error: ${e.message}"))
            }
        }
    }
}

private fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}
