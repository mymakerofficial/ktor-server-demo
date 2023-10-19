package de.maiker.plugins

import de.maiker.routes.apiRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        apiRouting()
    }
}