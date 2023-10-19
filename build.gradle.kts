val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project
val postgresql_version: String by project
val hikaricp_version: String by project
val liquibase_core: String by project
val koin_version: String by project


plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"

    // database migration
    id("org.liquibase.gradle") version "2.0.4"
}

group = "de.maiker"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-swagger-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // database
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")

    // koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    // liquibase
    liquibaseRuntime("org.liquibase:liquibase-core:$liquibase_core")
    liquibaseRuntime("org.postgresql:postgresql:$postgresql_version")
    liquibaseRuntime("ch.qos.logback:logback-core:$logback_version")
    liquibaseRuntime("ch.qos.logback:logback-classic:$logback_version")
    liquibaseRuntime("javax.xml.bind:jaxb-api:2.2.4")

    // testing
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

// database migration
liquibase {
    activities {
         mapOf(
            "logLevel" to "info",
            "changeLogFile" to "src/main/resources/db/migration/changelog.sql",
            "url" to "jdbc:postgresql://localhost:5432/postgres",
            "username" to "postgres",
            "password" to "postgres"
        )
    }
}