package de.maiker.crud

import de.maiker.exceptions.MediaFileNotFoundException
import de.maiker.models.MediaFileDto
import de.maiker.persistence.MediaFilePersistence
import java.util.*

class MediaFileCrudService(
    private val persistence: MediaFilePersistence,
) {
    suspend fun getMediaFileById(fileId: UUID): MediaFileDto {
        val file = persistence.getMediaFileById(fileId)

        if (file === null) {
            throw MediaFileNotFoundException(fileId)
        }

        return file
    }

    suspend fun getAllMediaFilesByMediaId(mediaId: UUID) = persistence.getAllMediaFilesByMediaId(mediaId)

    suspend fun createMediaFile(mediaFile: MediaFileDto) = persistence.createMediaFile(mediaFile)

    suspend fun deleteMediaFileById(fileId: UUID) = persistence.deleteMediaFileById(fileId)
}