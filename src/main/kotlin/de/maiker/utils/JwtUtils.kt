package de.maiker.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.util.*

fun ApplicationCall.getAuthenticatedUserId() = UUID.fromString(principal<UserIdPrincipal>()?.name) ?: throw IllegalArgumentException("No user id found")