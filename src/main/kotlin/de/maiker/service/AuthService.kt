package de.maiker.service;

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*
import kotlin.time.Duration.Companion.days

class AuthService(
    private val secret: String,
    private val lifetime: kotlin.time.Duration = 1.days,
) {
    private val algorithm = Algorithm.HMAC256(secret)
    private val verifier = JWT.require(algorithm).build()

    fun sign(claim: String, value: String): String {
        return JWT.create()
            .withClaim(claim, value)
            .withExpiresAt(Date(System.currentTimeMillis() + lifetime.inWholeMilliseconds))
            .sign(algorithm)
    }

    fun decode(token: String): DecodedJWT {
        return verifier.verify(token)
    }
}
