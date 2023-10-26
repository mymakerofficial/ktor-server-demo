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
import io.ktor.server.util.*
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
            val filePart = call.receiveMultipart().getFilePart() ?: return@post call.respond(HttpStatusCode.BadRequest)

            val file = fileService.createFile(
                userId,
                originalFileName = filePart.originalFileName ?: "",
                mimeType = filePart.contentType ?: ContentType.Application.OctetStream,
                fileBytes = filePart.streamProvider().readBytes()
            )

            call.respond(file.toResponse())
        }

        get("/files/{id}/raw") {
            val userId = call.getAuthenticatedUserId()
            val fileId = call.parameters.getOrFail<UUID>("id")

            val file = fileService.getFileByIdAndUserId(fileId, userId) ?: return@get call.respond(HttpStatusCode.NotFound)

            val fileBytes = fileService.readFile(file)

            call.response.header("Content-Disposition", "attachment; filename=\"${file.originalFileName}\"")
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