package de.maiker.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*

class JwtUtils {
    // TODO: make configurable

    private val algorithm = Algorithm.HMAC256("secret")

    fun <T> sign(id: T): String {
        return JWT.create()
        .withAudience("audience")
        .withIssuer("issuer")
        .withClaim("user_id", id.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
        .sign(algorithm)
    }
}

fun ApplicationCall.getAuthenticatedUserId() = UUID.fromString(principal<UserIdPrincipal>()?.name) ?: throw IllegalArgumentException("No user id found")