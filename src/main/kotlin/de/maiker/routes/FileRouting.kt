package de.maiker.routes

import de.maiker.service.MediaFileService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.fileRouting(
    mediaFileService: MediaFileService,
) {
    route("/file", {
        tags = listOf("Files")
    }) {
        get("/{token}/raw", {
            summary = "get a file by its token"
            request {
                pathParameter<String>("token") {
                    description = "the token granted by the server to access the file"
                }
            }
            response {
                HttpStatusCode.OK to {
                    body {
                        mediaType(ContentType.Any)
                        description = "the file"
                    }
                    header<String>(HttpHeaders.ContentDisposition) {
                        description = "the file name"
                    }
                }
                HttpStatusCode.InternalServerError to { description = "server failed to load file" }
            }
        }) {
            val token = call.parameters.getOrFail<String>("token")

            val (file, fileBytes) = mediaFileService.readMediaFileByToken(token)

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