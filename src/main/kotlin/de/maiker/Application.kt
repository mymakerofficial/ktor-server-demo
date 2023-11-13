package de.maiker

import de.maiker.database.DatabaseFactory
import de.maiker.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    configureResources()
    configureSecurity()
    configureHTTP()
    configureOpenApi()
    configureSerialization()
    configureRouting()
    configureStatus()

    DatabaseFactory.connect()
}
