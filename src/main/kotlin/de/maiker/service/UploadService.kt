package de.maiker.service

import de.maiker.models.MediaDto
import io.ktor.http.*
import java.io.File
import java.util.*

class UploadService {
    val mediaService = MediaService()
    val mediaFileService = MediaFileService()

    val uploadsPath = "uploads"

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
}