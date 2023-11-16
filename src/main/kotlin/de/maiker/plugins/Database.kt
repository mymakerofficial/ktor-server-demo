package de.maiker.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.connectDatabase(
    dbUrl: String = "jdbc:postgresql://localhost:5432/postgres",
    dbUser: String = "postgres",
    dbPassword: String = "postgres",
) {
    val config = HikariConfig()

    config.apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = dbUrl
        username = dbUser
        password = dbPassword
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }.run { validate() }

    Database.connect(HikariDataSource(config))
}