package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.exceptions.MediaNotFoundException
import de.maiker.mapper.toDto
import de.maiker.models.MediaDao
import de.maiker.models.MediaFileDao
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaFiles
import java.util.*

class MediaFilePersistence {
    suspend fun getMediaFileById(id: UUID): MediaFileDto? = dbQuery {
        MediaFileDao.findById(id)?.toDto()
    }

    suspend fun getAllMediaFilesByMediaId(mediaId: UUID): List<MediaFileDto> = dbQuery {
        MediaFileDao.find { MediaFiles.mediaId eq mediaId }.toList().map { it.toDto() }
    }

    suspend fun getMediaFileByContentHash(contentHash: String): MediaFileDto? = dbQuery {
        MediaFileDao.find { MediaFiles.contentHash eq contentHash }.firstOrNull()?.toDto()
    }

    suspend fun getAllOrphanedMediaFiles(): List<MediaFileDto> = dbQuery {
        MediaFileDao.find { MediaFiles.mediaId.isNull() }.toList().map { it.toDto() }
    }

    suspend fun createMediaFile(
        mediaId: UUID,
        contentHash: String,
        contentSize: Int,
        contentType: String,
        width: Int?,
        height: Int?,
    ): MediaFileDto = dbQuery {
        val media = MediaDao.findById(mediaId) ?: throw MediaNotFoundException(mediaId)

        MediaFileDao.new {
            this.media = media
            this.contentHash = contentHash
            this.contentSize = contentSize
            this.contentType = contentType
            this.width = width
            this.height = height
        }.toDto()
    }

    /**
     * Deletes a media file by its id from the database. This does not delete the actual file from the file system and may only be called after the file has been deleted from the file system.
     */
    suspend fun deleteMediaFileById(fileId: UUID) = dbQuery {
        MediaFileDao.findById(fileId)?.delete()
    }
}