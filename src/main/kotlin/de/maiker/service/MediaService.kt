package de.maiker.service

import de.maiker.crud.MediaCrudService
import de.maiker.mapper.toSignedDto
import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaSignedDto
import java.util.*

class MediaService(
    private val crudService: MediaCrudService = MediaCrudService(),
    private val mediaFileService: MediaFileService = MediaFileService(),
) {
    suspend fun getAllMediaByUserId(userId: UUID) = crudService.getAllMediaByUserId(userId)

    fun enrichFilesWithToken(media: MediaDto): MediaSignedDto {
        check(media.id != null)
        val files = mediaFileService.enrichWithToken(media.files)
        return media.toSignedDto(files)
    }

    fun enrichWithTokens(media: List<MediaDto>) = media.map { enrichFilesWithToken(it) }

    suspend fun createMedia(userId: UUID, originalFileName: String) = crudService.createMedia(userId, originalFileName)

    suspend fun getMediaById(mediaId: UUID) = crudService.getMediaById(mediaId)

    suspend fun getMediaByIdAndUserId(mediaId: UUID, userId: UUID) = crudService.getMediaByIdAndUserId(mediaId, userId)

    suspend fun deleteMediaById(mediaId: UUID) {
        val files = mediaFileService.getAllMediaFilesByMediaId(mediaId)
        files.forEach {
            check(it.id != null)
            mediaFileService.deleteMediaFileById(it.id)
        }
        crudService.deleteMediaById(mediaId)
    }
}