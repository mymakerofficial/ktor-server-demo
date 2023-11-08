package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.mapper.toDto
import de.maiker.models.*
import java.util.*

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
        val user = UserDao.findById(userId) ?: throw IllegalArgumentException("User with id $userId not found")

        MediaDao.new {
            this.originalFileName = originalFileName
            this.owner = user
        }.toDto()
    }

//    suspend fun createFile(userId: UUID, originalFileName: String, fileHash: String, fileSize: Int, mimeType: ContentType) = dbQuery {
//        val user = UserDao.findById(userId) ?: throw IllegalArgumentException("User with id $userId not found")
//
//        MediaDao.new {
//            this.originalFileName = originalFileName
//            this.fileHash = fileHash
//            this.fileSize = fileSize
//            this.mimeType = mimeType.toString()
//            this.owner = user
//        }.toDto()
//    }

    suspend fun deleteMediaById(fileId: UUID) = dbQuery {
        val media = MediaDao.findById(fileId) ?: throw IllegalArgumentException("Media with id $fileId not found")

        media.delete()
    }
}
