package de.maiker.plugins

import io.ktor.server.application.*
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType

fun Application.configureOpenApi() {
    install(SwaggerUI) {
        defaultSecuritySchemeName = "UserJwtAuth"
        defaultUnauthorizedResponse {
            description = "Unauthorized"
        }
        info {
            title = "ktor-server-demo"
            version = "latest"
            description = "a project to demonstrate the usage of ktor"
        }
        securityScheme("UserJwtAuth") {
            type = AuthType.HTTP
            scheme = AuthScheme.BEARER
            bearerFormat = "jwt"

        }
        tag("Auth") {
            description = "User authentication"
        }
        tag("Users") {
            description = "Operations about users"
        }
        tag("Media") {
            description = "Operations about media"
        }
        tag("Files") {
            description = "Operations about files"
        }
    }
}