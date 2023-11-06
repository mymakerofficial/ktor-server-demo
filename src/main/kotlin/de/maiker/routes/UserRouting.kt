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

    route("/users") {

        route("/me") {

            authenticate {

                get {
                    val userId = call.getAuthenticatedUserId()

                    val user = userService.getUserById(userId).getOrElse {
                        return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
                    }

                    call.respond(user.toResponse())
                }

                delete {
                    val userId = call.getAuthenticatedUserId()

                    userService.deleteUserById(userId)
                        .onFailure { return@delete call.respond(HttpStatusCode.InternalServerError, it.message.toString()) }
                        .onSuccess { return@delete call.respond(HttpStatusCode.OK) }
                }

            }

        }

        get {
            val users = userService.getAllUsers().getOrElse {
                return@get call.respond(HttpStatusCode.InternalServerError)
            }

            call.respond(users.toResponse())
        }

        get("/{id}") {
            val id = call.parameters.getOrFail<UUID>("id")

            val user = userService.getUserById(id).getOrElse {
                return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
            }

            call.respond(user.toResponse())
        }

        post("/register") {
            val request = call.receive<UserAuthRequest>()

            val user = userService.createUser(request.username, request.password).getOrElse {
                return@post call.respond(HttpStatusCode.Conflict, it.message.toString())
            }

            call.respond(user.toResponse())
        }

        post("/login") {
            val request = call.receive<UserAuthRequest>()

            val user = userService.getUserWithMatchingPassword(request.username, request.password).getOrElse {
                return@post call.respond(HttpStatusCode.Unauthorized, it.message.toString())
            }

            val token = jwtUtils.sign("user_id", user.id)

            call.respond(user.toResponse().withToken(token))
        }

    }
}