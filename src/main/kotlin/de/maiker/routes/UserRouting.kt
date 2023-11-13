package de.maiker.routes

import de.maiker.mapper.toResponse
import de.maiker.models.UserResponse
import de.maiker.crud.UserCrudService
import de.maiker.service.ContentService
import de.maiker.utils.getAuthenticatedUserId
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.pipeline.*
import java.util.*

fun Route.userRouting() {
    val userCrudService = UserCrudService()
    val contentService = ContentService()

    route("/users", {
        tags = listOf("Users")
    }) {
        authenticate {
            get("/me", {
                summary = "get information about the currently authenticated user"
                response {
                    HttpStatusCode.OK to { body<UserResponse>() }
                    HttpStatusCode.NotFound to { description = "user not found" }
                }
            }) {
                val userId = call.getAuthenticatedUserId()
                val user = userCrudService.getUserById(userId)
                call.respond(user.toResponse())
            }

            delete("/me", {
                summary = "delete current user and all associated data"
                response {
                    HttpStatusCode.OK to { description = "user and all associated data was successfully deleted" }
                    HttpStatusCode.InternalServerError to { description = "an unknown error occurred while deleting" }
                }
            }) {
                val userId = call.getAuthenticatedUserId()
                contentService.deleteUserCascadingById(userId)
            }

            get({
                summary = "get a list of all registered users"
                response {
                    HttpStatusCode.OK to { body<List<UserResponse>>() }
                    HttpStatusCode.InternalServerError to { description = "server failed to load users" }
                }
            }) {
                val users = userCrudService.getAllUsers()
                call.respond(users.toResponse())
            }

            get("/{id}", {
                summary = "get a specific user by id"
                request {
                    pathParameter<UUID>("id") {
                        description = "the id of the user"
                    }
                }
                response {
                    HttpStatusCode.OK to { body<UserResponse>() }
                    HttpStatusCode.NotFound to { description = "user not found" }
                }
            }) {
                val id = call.parameters.getOrFail<UUID>("id")
                val user = userCrudService.getUserById(id)
                call.respond(user.toResponse())
            }
        }
    }
}