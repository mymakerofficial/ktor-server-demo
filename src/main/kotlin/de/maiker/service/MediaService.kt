package de.maiker.service

import de.maiker.models.MediaDto
import de.maiker.persistence.MediaPersistence
import java.util.*
class MediaService {
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
            throw IllegalArgumentException("Media with id $mediaId not found")
        }

        media
    }

    suspend fun getFileByIdAndUserId(mediaId: UUID, userId: UUID): Result<MediaDto> = Result.runCatching {
        val media = mediaPersistence.getMediaByIdAndUserId(mediaId, userId)

        if (media === null) {
            throw IllegalArgumentException("Media with id $mediaId not found")
        }

        media
    }

//    fun readFileBytes(file: MediaDto): Result<ByteArray> = Result.runCatching {
//        File(getPath(file.owner.id, file.fileHash)).readBytes()
//    }

    suspend fun deleteMediaById(mediaId: UUID): Result<Unit> = Result.runCatching {
        mediaPersistence.deleteMediaById(mediaId)
    }

    suspend fun deleteMediaIfBelongsToUser(mediaId: UUID, userId: UUID): Result<Unit> = Result.runCatching {
        val media = mediaPersistence.getMediaByIdAndUserId(mediaId, userId)

        if (media === null) {
            throw IllegalArgumentException("Media with id $mediaId not found")
        }

        mediaPersistence.deleteMediaById(mediaId)
    }

//    private fun getPath(userId: UUID, fileHash: String): String =
//        "uploads/$userId/$fileHash"

//    private fun createFileOnDiskSave(path: String, fileBytes: ByteArray) {
//        val file = File(path)
//
//        file.parentFile.mkdirs()
//        file.writeBytes(fileBytes)
//    }

    // delete file from disk if no file in database has the same path
//    private suspend fun deleteFileOnDiskSave(path: String) {
//        val userId = UUID.fromString(path.split("/")[1])
//        val fileHash = path.split("/")[2]
//
//        val isInDatabase = mediaPersistence.countFilesByUserIdAndHash(userId, fileHash) > 0
//
//        if (isInDatabase) {
//            return
//        }
//
//        File(path).delete()
//    }
}

