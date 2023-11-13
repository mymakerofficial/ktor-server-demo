package de.maiker.crud

import de.maiker.exceptions.MediaNotFoundException
import de.maiker.models.MediaDto
import de.maiker.persistence.MediaPersistence
import java.util.*
class MediaCrudService {
    private val mediaPersistence = MediaPersistence()

    suspend fun getAllMediaByUserId(userId: UUID): Result<List<MediaDto>> = Result.runCatching {
        mediaPersistence.getAllMediaByUserId(userId)
    }

    suspend fun createMedia(userId: UUID, originalFileName: String): Result<MediaDto> = Result.runCatching {
        mediaPersistence.createMedia(userId, originalFileName)
    }

    suspend fun getMediaById(mediaId: UUID): Result<MediaDto> = Result.runCatching {
        val media = mediaPersistence.getMediaById(mediaId)

        if (media === null) {
            throw MediaNotFoundException(mediaId)
        }

        media
    }

    suspend fun getMediaByIdAndUserId(mediaId: UUID, userId: UUID): Result<MediaDto> = Result.runCatching {
        val media = mediaPersistence.getMediaByIdAndUserId(mediaId, userId)

        if (media === null) {
            throw MediaNotFoundException(mediaId)
        }

        media
    }

    suspend fun deleteMediaById(mediaId: UUID): Result<Unit> = Result.runCatching {
        mediaPersistence.deleteMediaById(mediaId)
    }

    suspend fun deleteMediaIfBelongsToUser(mediaId: UUID, userId: UUID): Result<Unit> = Result.runCatching {
        val media = mediaPersistence.getMediaByIdAndUserId(mediaId, userId)

        if (media === null) {
            throw MediaNotFoundException(mediaId)
        }

        mediaPersistence.deleteMediaById(mediaId)
    }
}

