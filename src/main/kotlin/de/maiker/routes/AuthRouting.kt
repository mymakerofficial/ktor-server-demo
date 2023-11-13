package de.maiker.routes

import de.maiker.mapper.toResponse
import de.maiker.mapper.withToken
import de.maiker.models.UserAuthRequest
import de.maiker.models.UserLoginResponse
import de.maiker.models.UserResponse
import de.maiker.service.UserAuthService
import de.maiker.crud.UserCrudService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRouting() {
    val userCrudService = UserCrudService()
    val authService = UserAuthService()

    route("/auth", {
        tags = listOf("Auth")
    }) {
        post("/register", {
            summary = "register a new user"
            request {
                body<UserAuthRequest>()
            }
            response {
                HttpStatusCode.OK to { body<UserResponse>() }
                HttpStatusCode.Conflict to { description = "user with given username already exists" }
            }
        }) {
            val request = call.receive<UserAuthRequest>()
            val user = userCrudService.createUser(request.username, request.password)
            call.respond(user.toResponse())
        }

        post("/login", {
            summary = "login a user"
            request {
                body<UserAuthRequest>()
            }
            response {
                HttpStatusCode.OK to { body<UserLoginResponse>() }
                HttpStatusCode.Unauthorized to { description = "username or password is incorrect" }
            }
        }) {
            val request = call.receive<UserAuthRequest>()
            val (token, user) = authService.authenticate(request.username, request.password)
            call.respond(user.toResponse().withToken(token))
        }
    }
}