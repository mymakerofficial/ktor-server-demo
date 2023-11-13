package de.maiker.plugins

import de.maiker.exceptions.AccessDeniedException
import de.maiker.exceptions.UserAlreadyExistsException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val code: String,
    val exception: String? = null,
)

fun statusCodeFromException(exception: Throwable): HttpStatusCode {
    return when (exception) {
        is IllegalArgumentException -> HttpStatusCode.BadRequest
        is NoSuchElementException -> HttpStatusCode.NotFound
        is AccessDeniedException -> HttpStatusCode.Forbidden
        is UserAlreadyExistsException -> HttpStatusCode.Conflict
        else -> HttpStatusCode.InternalServerError
    }
}

fun Application.configureStatus() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val statusCode = statusCodeFromException(cause)

            call.respond(statusCode, ErrorResponse(
                message = cause.message ?: "Unknown error",
                code = statusCode.value.toString(),
                exception = cause::class.simpleName
            ))
        }
    }
}