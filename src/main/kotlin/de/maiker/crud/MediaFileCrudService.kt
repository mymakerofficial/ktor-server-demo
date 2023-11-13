package de.maiker.crud

import de.maiker.exceptions.MediaFileNotFoundException
import de.maiker.models.MediaFileDto
import de.maiker.persistence.MediaFilePersistence
import java.util.*

class MediaFileCrudService {
    private val mediaFilePersistence = MediaFilePersistence()

    suspend fun getMediaFileById(fileId: UUID): Result<MediaFileDto> = Result.runCatching {
        val file = mediaFilePersistence.getMediaFileById(fileId)

        if (file === null) {
            throw MediaFileNotFoundException(fileId)
        }

        file
    }

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