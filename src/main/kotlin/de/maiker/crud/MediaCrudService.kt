package de.maiker.crud

import de.maiker.exceptions.MediaNotFoundException
import de.maiker.models.MediaDto
import de.maiker.persistence.MediaPersistence
import java.util.*
class MediaCrudService(
    private val mediaPersistence: MediaPersistence = MediaPersistence()
) {
    suspend fun getAllMediaByUserId(userId: UUID) = mediaPersistence.getAllMediaByUserId(userId)

    suspend fun createMedia(userId: UUID, originalFileName: String) = mediaPersistence.createMedia(userId, originalFileName)

    suspend fun getMediaById(mediaId: UUID): MediaDto {
        val media = mediaPersistence.getMediaById(mediaId)

        if (media === null) {
            throw MediaNotFoundException(mediaId)
        }

        return media
    }

    suspend fun getMediaByIdAndUserId(mediaId: UUID, userId: UUID): MediaDto {
        val media = mediaPersistence.getMediaByIdAndUserId(mediaId, userId)

        if (media === null) {
            throw MediaNotFoundException(mediaId)
        }

        return media
    }

    suspend fun deleteMediaById(mediaId: UUID) = mediaPersistence.deleteMediaById(mediaId)
}

