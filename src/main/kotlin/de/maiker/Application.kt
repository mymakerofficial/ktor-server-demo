package de.maiker

import de.maiker.database.DatabaseFactory
import de.maiker.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureResources()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()

    DatabaseFactory.connect()
}
