package de.maiker.routes

import de.maiker.mapper.toResponse
import de.maiker.service.FileService
import de.maiker.utils.getAuthenticatedUserId
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.fileRouting() {
    val fileService = FileService()

    authenticate {

        get("/files") {
            val userId = call.getAuthenticatedUserId()
            val files = fileService.getAllFilesByUserId(userId).map { it.toResponse() }
            call.respond(files)
        }

        post("/files") {
            val userId = call.getAuthenticatedUserId()
            val multipart = call.receiveMultipart()

            var fileName = ""
            var mimeType = ""
            var fileBytes = ByteArray(0)

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        mimeType = part.contentType.toString()
                        fileBytes = part.streamProvider().readBytes()
                    }

                    else -> {}
                }
                part.dispose()
            }

            val file = fileService.createFile(userId, fileName, mimeType, fileBytes)

            call.respond(file.toResponse())
        }

        get("/files/{id}/original") {
            val fileId = runCatching { UUID.fromString(call.parameters["id"]) }.getOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val userId = call.getAuthenticatedUserId()

            val file = fileService.getFileById(fileId) ?: return@get call.respond(HttpStatusCode.NotFound)

            if (file.userId != userId) {
                return@get call.respond(HttpStatusCode.Forbidden)
            }

            val fileBytes = fileService.readFile(file)

            call.respondBytes(fileBytes, ContentType.parse(file.mimeType))
        }
    }
}