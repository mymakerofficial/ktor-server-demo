package de.maiker.service

import de.maiker.crud.MediaFileCrudService
import de.maiker.mapper.toSignedDto
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaFileSignedDto
import de.maiker.storage.StorageFactory
import de.maiker.storage.StorageSpec
import de.maiker.utils.asUUID
import java.nio.file.Path
import java.util.*

class MediaFileService(
    private val crudService: MediaFileCrudService,
    private val authService: AuthService,
    private val storageFactory: StorageFactory,
) {
    private val uploadsPath = "uploads"
    private val mediaFileClaim = "fid"

    private val storage = storageFactory.createStorage()

    private fun getPath(contentHash: String) = Path.of(uploadsPath, contentHash)

    suspend fun getMediaFileById(fileId: UUID) = crudService.getMediaFileById(fileId)

    suspend fun getAllMediaFilesByMediaId(mediaId: UUID) = crudService.getAllMediaFilesByMediaId(mediaId)

    fun enrichWithToken(mediaFile: MediaFileDto): MediaFileSignedDto {
        check(mediaFile.id != null)

        val token = authService.sign(mediaFileClaim, mediaFile.id.toString())
        return mediaFile.toSignedDto(token)
    }

    fun enrichWithToken(mediaFiles: List<MediaFileDto>) = mediaFiles.map { enrichWithToken(it) }

    fun readMediaFile(mediaFile: MediaFileDto): ByteArray {
        return storage.readBytes(getPath(mediaFile.contentHash))
    }

    suspend fun readMediaFileById(fileId: UUID): Pair<MediaFileDto, ByteArray> {
        val mediaFile = getMediaFileById(fileId)
        return mediaFile to readMediaFile(mediaFile)
    }

    suspend fun readMediaFileByToken(token: String): Pair<MediaFileDto, ByteArray> {
        val fileId = authService.decode(token).getClaim(mediaFileClaim).asUUID()
        return readMediaFileById(fileId)
    }

    suspend fun createMediaFile(mediaFile: MediaFileDto, fileBytes: ByteArray): MediaFileDto {
        storage.writeBytes(getPath(mediaFile.contentHash), fileBytes)
        return crudService.createMediaFile(mediaFile)
    }

    suspend fun deleteMediaFileById(fileId: UUID) {
        val mediaFile = crudService.getMediaFileById(fileId)
        storage.deleteFile(getPath(mediaFile.contentHash))
        crudService.deleteMediaFileById(fileId)
    }
}