package de.maiker.service;

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*
import kotlin.time.Duration.Companion.days

class AuthService {
    private val secret = "secret";
    private val issuer = "issuer";
    private val audience = "audience";

    private val lifetime = 1.days;

    private val algorithm = Algorithm.HMAC256(secret);
    private val verifier = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun sign(claim: String, value: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(claim, value)
            .withExpiresAt(Date(System.currentTimeMillis() + lifetime.inWholeMilliseconds))
            .sign(algorithm)
    }

    fun decode(token: String): DecodedJWT {
        return verifier.verify(token)
    }
}
