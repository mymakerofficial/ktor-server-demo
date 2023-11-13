package de.maiker.crud

import de.maiker.exceptions.MediaFileNotFoundException
import de.maiker.models.MediaFileDto
import de.maiker.persistence.MediaFilePersistence
import java.util.*

class MediaFileCrudService(
    private val mediaFilePersistence: MediaFilePersistence = MediaFilePersistence()
) {
    suspend fun getMediaFileById(fileId: UUID): MediaFileDto {
        val file = mediaFilePersistence.getMediaFileById(fileId)

        if (file === null) {
            throw MediaFileNotFoundException(fileId)
        }

        return file
    }

    suspend fun getAllMediaFilesByMediaId(mediaId: UUID) = mediaFilePersistence.getAllMediaFilesByMediaId(mediaId)

    suspend fun createMediaFile(
        mediaId: UUID,
        contentHash: String,
        contentSize: Int,
        contentType: String,
        width: Int?,
        height: Int?,
    ) = mediaFilePersistence.createMediaFile(
        mediaId,
        contentHash,
        contentSize,
        contentType,
        width,
        height
    )

    suspend fun deleteMediaFileById(fileId: UUID) = mediaFilePersistence.deleteMediaFileById(fileId)
}