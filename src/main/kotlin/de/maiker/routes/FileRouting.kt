package de.maiker.routes

import de.maiker.service.ContentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.util.*

fun Route.fileRouting() {
    val contentService = ContentService()

    route("/file") {
        get("/{id}/raw") {
            val fileId = call.parameters.getOrFail<UUID>("id")
            val token = call.request.queryParameters.getOrFail<String>("token")

            val (file, fileBytes) = contentService.getFileByIdWithAuthentication(fileId, token).getOrElse {
                return@get call.respond(HttpStatusCode.InternalServerError, it.message.toString())
            }

            val fileName = "${file.id}.${file.contentType.contentSubtype}"

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Inline.withParameter(
                    ContentDisposition.Parameters.FileName,
                    fileName
                ).toString()
            )
            call.respondBytes(fileBytes, file.contentType)
        }
    }
}