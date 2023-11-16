package de.maiker.routes

import de.maiker.service.*
import io.ktor.server.routing.*

fun Route.apiRouting() {
    route("/api") {
        authRouting()
        userRouting()
        mediaRouting()
        fileRouting()
    }
}