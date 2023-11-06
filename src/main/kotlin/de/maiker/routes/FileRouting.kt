package de.maiker.routes

import de.maiker.mapper.toListResponse
import de.maiker.mapper.toResponse
import de.maiker.mapper.withUrl
import de.maiker.service.FileService
import de.maiker.utils.JwtUtils
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

fun Route.fileRouting() {
    val fileService = FileService()
    val jwtUtils = JwtUtils()

    fun getAuthenticatedFileUrl(fileId: UUID): String {
        val id = fileId.toString()
        val token = jwtUtils.sign("file_id", id)
        return "http://localhost:8080/api/files/$id/raw?token=$token"
    }

    route("/files") {

        authenticate {

            get {
                val userId = call.getAuthenticatedUserId()

                val files = fileService.getAllFilesByUserId(userId).getOrElse {
                    return@get call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }

                call.respond(files.map { it.toListResponse().withUrl(getAuthenticatedFileUrl(it.id)) })
            }

            post {
                val userId = call.getAuthenticatedUserId()
                val filePart =
                    call.receiveMultipart().getFilePart() ?: return@post call.respond(HttpStatusCode.BadRequest)

                val file = fileService.createFile(
                    userId,
                    originalFileName = filePart.originalFileName ?: "",
                    mimeType = filePart.contentType ?: ContentType.Application.OctetStream,
                    fileBytes = filePart.streamProvider().readBytes()
                ).getOrElse {
                    return@post call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                }

                val url = getAuthenticatedFileUrl(file.id)

                call.respond(file.toListResponse().withUrl(url))
            }

            route("/{id}") {

                get {
                    val fileId = call.parameters.getOrFail<UUID>("id")
                    val userId = call.getAuthenticatedUserId()

                    val file = fileService.getFileByIdAndUserId(fileId, userId).getOrElse {
                        return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
                    }

                    val url = getAuthenticatedFileUrl(file.id)

                    call.respond(file.toResponse(url))
                }

                delete {
                    val fileId = call.parameters.getOrFail<UUID>("id")
                    val userId = call.getAuthenticatedUserId()

                    // check if file belongs to user
                    fileService.getFileByIdAndUserId(fileId, userId).onFailure {
                        return@delete call.respond(HttpStatusCode.NotFound, it.message.toString())
                    }

                    // delete file
                    fileService.deleteFileById(fileId).onFailure {
                        return@delete call.respond(HttpStatusCode.InternalServerError, it.message.toString())
                    }

                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        get("/{id}/raw") {
            val fileId = call.parameters.getOrFail<UUID>("id")
            val token = call.request.queryParameters.getOrFail<String>("token")

            val tokenFileId = jwtUtils.decode(token).getClaim("file_id").asString()

            if (fileId.toString() != tokenFileId) {
                return@get call.respond(HttpStatusCode.Unauthorized)
            }

            val file = fileService.getFileById(fileId).getOrElse {
                return@get call.respond(HttpStatusCode.NotFound, it.message.toString())
            }

            val fileBytes = fileService.readFileBytes(file).getOrElse {
                return@get call.respond(HttpStatusCode.InternalServerError, it.message.toString())
            }

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Inline.withParameter(ContentDisposition.Parameters.FileName, file.originalFileName)
                    .toString()
            )
            call.respondBytes(fileBytes, file.mimeType)
        }

    }
}

suspend fun MultiPartData.getFilePart(): PartData.FileItem? {
    var filePart: PartData.FileItem? = null

    forEachPart { part ->
        when (part) {
            is PartData.FileItem -> filePart = part
            else -> part.dispose()
        }
    }

    return filePart
}