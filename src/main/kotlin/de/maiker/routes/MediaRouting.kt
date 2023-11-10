package de.maiker.routes

import de.maiker.mapper.toListResponse
import de.maiker.mapper.toResponse
import de.maiker.models.MediaListResponse
import de.maiker.models.MediaResponse
import de.maiker.service.MediaService
import de.maiker.service.ContentService
import de.maiker.utils.getAuthenticatedUserId
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
    val mediaService = MediaService()
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

                val media = mediaService.getAllMediaByUserId(userId).getOrElse {
                    return@get call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }.toListResponse()

                call.respond(media)
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

                val media = mediaService.getMediaByIdAndUserId(mediaId, userId).getOrElse {
                    return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
                }

                call.respond(media.toResponse())
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