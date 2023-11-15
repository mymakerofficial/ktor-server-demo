package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.exceptions.MediaNotFoundException
import de.maiker.exceptions.UserNotFoundException
import de.maiker.mapper.toDto
import de.maiker.models.*
import java.util.*
import kotlin.contracts.contract

class MediaPersistence {
    suspend fun getAllMediaByUserId(userId: UUID): List<MediaDto> = dbQuery {
        MediaDao.find { Media.ownerId eq userId }.toList().map { it.toDto() }
    }

    suspend fun getMediaById(id: UUID): MediaDto? = dbQuery {
        MediaDao.findById(id)?.toDto()
    }

    suspend fun getMediaByIdAndUserId(mediaId: UUID, userId: UUID): MediaDto? = dbQuery {
        MediaDao.find {
            Media.id eq mediaId
            Media.ownerId eq userId
        }.firstOrNull()?.toDto()
    }

    suspend fun createMedia(userId: UUID, originalFileName: String): MediaDto = dbQuery {
        val user = UserDao.findById(userId) ?: throw UserNotFoundException(userId)

        MediaDao.new {
            this.originalFileName = originalFileName
            this.owner = user
        }.toDto()
    }

    suspend fun deleteMediaById(fileId: UUID) = dbQuery {
        val media = MediaDao.findById(fileId) ?: throw MediaNotFoundException(fileId)

        media.delete()
    }
}
