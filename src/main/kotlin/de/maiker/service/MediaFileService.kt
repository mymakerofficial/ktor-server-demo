package de.maiker.service

import de.maiker.models.MediaFileDto
import de.maiker.persistence.MediaFilePersistence
import java.util.*

class MediaFileService {
    private val mediaFilePersistence = MediaFilePersistence()

    suspend fun getAllMediaFilesByMediaId(mediaId: UUID): Result<List<MediaFileDto>> = Result.runCatching {
        mediaFilePersistence.getAllMediaFilesByMediaId(mediaId)
    }

    suspend fun createMediaFile(
        mediaId: UUID,
        contentHash: String,
        contentSize: Int,
        contentType: String,
        width: Int?,
        height: Int?,
    ): Result<MediaFileDto> = Result.runCatching {
        mediaFilePersistence.createMediaFile(mediaId, contentHash, contentSize, contentType, width, height)
    }

    suspend fun deleteMediaFileById(fileId: UUID): Result<Unit> = Result.runCatching {
        mediaFilePersistence.deleteMediaFileById(fileId)
    }
}