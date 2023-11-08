package de.maiker.routes

import io.ktor.server.routing.*

fun Route.apiRouting() {
    route("/api") {
        userRouting()
        mediaRouting()
    }
}