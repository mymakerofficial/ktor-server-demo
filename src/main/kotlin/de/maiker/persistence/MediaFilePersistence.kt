package de.maiker.persistence

import de.maiker.mapper.toDto
import de.maiker.models.MediaDao
import de.maiker.models.MediaFileDao
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaFiles
import de.maiker.utils.dbQuery
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

    suspend fun createMediaFile(mediaFile: MediaFileDto): MediaFileDto = dbQuery {
        check(mediaFile.mediaId != null) { "MediaFile.mediaId must not be null" }
        val media = MediaDao.findById(mediaFile.mediaId)
        check(media != null) { "Media with id ${mediaFile.mediaId} does not exist" }

        MediaFileDao.new {
            this.media = media
            this.contentHash = mediaFile.contentHash
            this.contentSize = mediaFile.contentSize
            this.contentType = mediaFile.contentType.toString()
            this.width = mediaFile.width
            this.height = mediaFile.height
        }.toDto()
    }

    /**
     * Deletes a media file by its id from the database. This does not delete the actual file from the file system and may only be called after the file has been deleted from the file system.
     */
    suspend fun deleteMediaFileById(fileId: UUID) = dbQuery {
        MediaFileDao.findById(fileId)?.delete()
    }
}