package de.maiker

import de.maiker.plugins.*
import de.maiker.routes.apiRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    configureSecurity()
    configureCORS()
    configureOpenApi()
    configureSerialization()
    configureStatus()

    connectDatabase(
        dbUrl = "jdbc:postgresql://localhost:5432/postgres",
        dbUser = "postgres",
        dbPassword = "postgres",
    )

    routing {
        apiRouting()
    }
}
