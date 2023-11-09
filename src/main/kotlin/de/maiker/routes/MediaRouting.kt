package de.maiker.routes

import de.maiker.mapper.toListResponse
import de.maiker.mapper.toResponse
import de.maiker.service.MediaService
import de.maiker.service.ContentService
import de.maiker.utils.getAuthenticatedUserId
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.util.*

fun Route.mediaRouting() {
    val mediaService = MediaService()
    val contentService = ContentService()

    route("/media") {

        authenticate {

            get {
                val userId = call.getAuthenticatedUserId()

                val media = mediaService.getAllMediaByUserId(userId).getOrElse {
                    return@get call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }.toListResponse()

                call.respond(media)
            }

            post {
                val userId = call.getAuthenticatedUserId()
                val filePart = call.receiveMultipart().getFilePart().getOrElse {
                    return@post call.respond(HttpStatusCode.BadRequest, it.message.toString())
                }

                val originalFileName = filePart.originalFileName ?: ""
                val fileBytes = filePart.streamProvider().readBytes()
                val contentType = filePart.contentType ?: ContentType.Application.OctetStream

                val media = contentService.uploadMediaWithFile(
                    userId,
                    originalFileName,
                    fileBytes,
                    contentType,
                ).getOrElse {
                    return@post call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }

                call.respond(media.toResponse())
            }

            route("/{id}") {

                get {
                    val mediaId = call.parameters.getOrFail<UUID>("id")
                    val userId = call.getAuthenticatedUserId()

                    val media = mediaService.getMediaByIdAndUserId(mediaId, userId).getOrElse {
                        return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
                    }

                    call.respond(media.toResponse())
                }

//                delete {
//                    val mediaId = call.parameters.getOrFail<UUID>("id")
//                    val userId = call.getAuthenticatedUserId()
//
//                    mediaService.deleteMediaIfBelongsToUser(mediaId, userId).onFailure {
//                        return@delete call.respond(HttpStatusCode.InternalServerError, it.message.toString())
//                    }
//
//                    call.respond(HttpStatusCode.OK)
//                }
            }
        }
    }
}

suspend fun MultiPartData.getFilePart(): Result<PartData.FileItem> = Result.runCatching {
    var filePart: PartData.FileItem? = null

    forEachPart { part ->
        when (part) {
            is PartData.FileItem -> filePart = part
            else -> part.dispose()
        }
    }

    if (filePart === null) {
        throw Exception("No file part found")
    }

    filePart!!
}