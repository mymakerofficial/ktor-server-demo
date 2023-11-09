package de.maiker.service

import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.io.File
import java.util.*

class ContentService {
    val mediaService = MediaService()
    val mediaFileService = MediaFileService()
    val authService = AuthService()

    val uploadsPath = "uploads"
    val mediaFileClaim = "fid"

    suspend fun uploadMediaWithFile(userId: UUID, originalFileName: String, fileBytes: ByteArray, contentType: ContentType): Result<MediaDto> = Result.runCatching {
        val media = mediaService.createMedia(userId, originalFileName).getOrElse {
            throw Exception("Failed to create media entry")
        }

        val contentHash = fileBytes.contentHashCode().toString()
        val contentSize = fileBytes.size
        val filePath = "$uploadsPath/$contentHash"

        runCatching {
            File(filePath).writeBytes(fileBytes)
        }.onFailure {
            mediaService.deleteMediaById(media.id)
            throw Exception("Failed to save file to disk, media was not created")
        }

        mediaFileService.createMediaFile(
            mediaId = media.id,
            contentHash,
            contentSize,
            contentType = contentType.toString(),
            width = null,
            height = null
        ).getOrElse {
            File(filePath).delete()
            mediaService.deleteMediaById(media.id)
            throw Exception("Failed to create media file entry, media was not created")
        }

        media
    }

    suspend fun getFileByIdWithAuthentication(fileId: UUID, token: String): Result<Pair<MediaFileDto, ByteArray>> = Result.runCatching {
        val tokenFileId = runCatching {
            authService.decode(token).getClaim(mediaFileClaim).asString()
        }.getOrElse {
            throw Exception("Invalid token")
        }

        if (fileId.toString() != tokenFileId) {
            throw Exception("Token does not have claim for given media file id")
        }

        val file = mediaFileService.getMediaFileById(fileId).getOrThrow()

        val fileBytes = runCatching {
            File("$uploadsPath/${file.contentHash}").readBytes()
        }.getOrElse {
            throw Exception("Failed to read file from disk")
        }

        file to fileBytes
    }
}