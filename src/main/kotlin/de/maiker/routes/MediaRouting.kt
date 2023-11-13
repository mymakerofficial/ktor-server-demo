package de.maiker.routes

import de.maiker.mapper.toListResponse
import de.maiker.mapper.toResponse
import de.maiker.models.MediaListResponse
import de.maiker.models.MediaResponse
import de.maiker.crud.MediaCrudService
import de.maiker.service.ContentService
import de.maiker.utils.getAuthenticatedUserId
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.io.File
import java.util.*

fun Route.mediaRouting() {
    val mediaCrudService = MediaCrudService()
    val contentService = ContentService()

    route("/media", {
        tags = listOf("Media")
    }) {

        authenticate {

            get({
                summary = "get a list of all media owned by the currently authenticated user"
                response {
                    HttpStatusCode.OK to { body<List<MediaListResponse>>() }
                    HttpStatusCode.InternalServerError to { description = "server failed to load media" }
                }
            }) {
                val userId = call.getAuthenticatedUserId()

                val media = runCatching {
                    mediaCrudService.getAllMediaByUserId(userId)
                }.getOrElse {
                    return@get call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }

                call.respond(media.toListResponse())
            }

            post({
                summary = "upload a new media file"
                request {
                    multipartBody {
                        mediaType(ContentType.MultiPart.FormData)
                        part<File>("file") {
                            mediaTypes = setOf(
                                ContentType.Image.PNG,
                                ContentType.Image.JPEG,
                                ContentType.Video.MP4,
                                ContentType.Video.MPEG,
                            )
                        }
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MediaResponse>() }
                    HttpStatusCode.BadRequest to { description = "no file part found" }
                    HttpStatusCode.InternalServerError to { description = "server failed to upload media" }
                }
            }) {
                val userId = call.getAuthenticatedUserId()
                val filePart = call.receiveMultipart().getFilePart().getOrElse {
                    return@post call.respond(HttpStatusCode.BadRequest, it.message.toString())
                }

                val originalFileName = filePart.originalFileName ?: ""
                val fileBytes = filePart.streamProvider().readBytes()
                val contentType = filePart.contentType ?: ContentType.Application.OctetStream

                val media = runCatching {
                    contentService.uploadMediaWithFile(
                        userId,
                        originalFileName,
                        fileBytes,
                        contentType,
                    )
                }.getOrElse {
                    return@post call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }

                call.respond(media.toResponse())
            }

            get("/{id}", {
                summary = "get a specific media by id"
                request {
                    pathParameter<UUID>("id") {
                        description = "the id of the media"
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MediaResponse>() }
                    HttpStatusCode.NotFound to { description = "media not found" }
                }
            }) {
                val mediaId = call.parameters.getOrFail<UUID>("id")
                val userId = call.getAuthenticatedUserId()

                val media = runCatching {
                    mediaCrudService.getMediaByIdAndUserId(mediaId, userId)
                }.getOrElse {
                    return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
                }

                call.respond(media.toResponse())
            }

            delete("/{id}", {
                summary = "delete given media and all associated data"
                request {
                    pathParameter<UUID>("id") {
                        description = "the id of the media to delete"
                    }
                }
                response {
                    HttpStatusCode.OK to { description = "media and associated files were successfully deleted" }
                    HttpStatusCode.InternalServerError to { description = "an unknown error occurred while deleting" }
                }
            }) {
                val id = call.parameters.getOrFail<UUID>("id")

                contentService.deleteMediaCascadingById(id)
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