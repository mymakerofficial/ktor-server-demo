package de.maiker.service

import de.maiker.models.MediaDto
import io.ktor.http.*
import java.util.*

class UploadService {
    val mediaService = MediaService()
    val mediaFileService = MediaFileService()

    suspend fun uploadMediaWithFile(userId: UUID, originalFileName: String, fileBytes: ByteArray, contentType: ContentType): Result<MediaDto> = Result.runCatching {
        val media = mediaService.createMedia(userId, originalFileName).getOrElse {
            throw Exception("Failed to create media entry")
        }

        // TODO: Save file to disk
        // TODO: rip and store metadata
        // TODO: generate preview
        // TODO: figure out a way to distinguish between original and preview

        mediaFileService.createMediaFile(
            mediaId = media.id,
            contentHash = fileBytes.contentHashCode().toString(),
            contentSize = fileBytes.size,
            contentType = contentType.toString(),
            width = null,
            height = null
        ).getOrElse {
            mediaService.deleteMediaById(media.id)
            throw Exception("Failed to create media file entry, media was not created")
        }

        media
    }
}