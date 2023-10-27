package de.maiker.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*

class JwtUtils {
    // TODO: make configurable

    private val algorithm = Algorithm.HMAC256("secret")

    fun <T> sign(claim: String, id: T): String {
        return JWT.create()
        .withAudience("audience")
        .withIssuer("issuer")
        .withClaim(claim, id.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
        .sign(algorithm)
    }

    fun decode(token: String): DecodedJWT {
        val algorithm = Algorithm.HMAC256("secret")
        val verifier = JWT.require(algorithm)
            .withAudience("audience")
            .withIssuer("issuer")
            .build()

        return verifier.verify(token)
    }
}

fun ApplicationCall.getAuthenticatedUserId() = UUID.fromString(principal<UserIdPrincipal>()?.name) ?: throw IllegalArgumentException("No user id found")