package de.maiker.routes

import de.maiker.mapper.toResponse
import de.maiker.mapper.withToken
import de.maiker.models.UserAuthRequest
import de.maiker.service.UserService
import de.maiker.utils.JwtUtils
import de.maiker.utils.getAuthenticatedUserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import io.ktor.server.util.*
import java.util.*

fun Route.userRouting() {
    val userService = UserService()
    val jwtUtils = JwtUtils()

    get("/users") {
        val users = userService.getAllUsers().map { it.toResponse() }
        call.respond(users)
    }

    get("/users/{id}") {
        val id = call.parameters.getOrFail<UUID>("id")
        val user = userService.getUserById(id) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(user.toResponse())
    }

    post("/users/register") {
        val request = call.receive<UserAuthRequest>()
        val user = userService.createUser(request.username, request.password) ?: return@post call.respond(HttpStatusCode.Conflict)
        call.respond(user.toResponse())
    }

    post("/users/login") {
        val request = call.receive<UserAuthRequest>()
        val user = userService.loginAndGetUser(request.username, request.password) ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val token = jwtUtils.sign("user_id", user.id)
        call.respond(user.toResponse().withToken(token))
    }

    authenticate {

        get("/authenticated") {
            call.respond(HttpStatusCode.OK)
        }

        get("/user") {
            val userId = call.getAuthenticatedUserId()
            val user = userService.getUserById(userId) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(user.toResponse())
        }

        delete("/user") {
            val userId = call.getAuthenticatedUserId()
            userService.deleteUserById(userId)
            call.respond(HttpStatusCode.OK)
        }

    }
}