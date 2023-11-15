package de.maiker.utils

import com.auth0.jwt.interfaces.Claim
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*

fun ApplicationCall.getAuthenticatedUserId() = UUID.fromString(principal<UserIdPrincipal>()?.name) ?: throw IllegalArgumentException("No user id found")

fun Claim.asUUID() = UUID.fromString(this.asString()) ?: throw IllegalArgumentException("Claim is not a valid UUID")