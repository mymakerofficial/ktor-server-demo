package de.maiker.crud

import de.maiker.exceptions.MediaFileNotFoundException
import de.maiker.models.MediaFileDto
import de.maiker.persistence.MediaFilePersistence
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class MediaFileCrudService : KoinComponent {
    private val persistence: MediaFilePersistence by inject()

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