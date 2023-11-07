package de.maiker.service

import de.maiker.models.FileDto
import de.maiker.persistence.FilePersistence
import io.ktor.http.*
import java.io.File
import java.util.*
class FileService {
    private val filePersistence = FilePersistence()

    suspend fun getAllFilesByUserId(userId: UUID): Result<List<FileDto>> = Result.runCatching {
        filePersistence.getAllFilesByUserId(userId)
    }

    suspend fun createFile(userId: UUID, originalFileName: String, mimeType: ContentType, fileBytes: ByteArray): Result<FileDto> = Result.runCatching {
        val fileHash = fileBytes.contentHashCode().toString()
        val filePath = getPath(userId, fileHash)
        val fileSize = fileBytes.size

        // save file to disk
        createFileOnDiskSave(filePath, fileBytes)

        // save file to database
        val file = runCatching {
            filePersistence.createFile(
                originalFileName = originalFileName,
                fileHash = fileHash,
                fileSize = fileSize,
                mimeType = mimeType,
                userId = userId,
            )
        }.getOrElse {
            // delete file from disk if database insert fails
            deleteFileOnDiskSave(filePath)

            // rethrow exception
            throw it
        }

        file
    }

    suspend fun getFileById(fileId: UUID): Result<FileDto> = Result.runCatching {
        val file = filePersistence.getFileById(fileId)

        if (file === null) {
            throw IllegalArgumentException("File with id $fileId not found")
        }

        file
    }

    suspend fun getFileByIdAndUserId(fileId: UUID, userId: UUID): Result<FileDto> = Result.runCatching {
        val file = getFileById(fileId).getOrElse { throw it }

        if (file.user.id != userId) {
            throw IllegalArgumentException("File with id $fileId not found")
        }

        file
    }

    fun readFileBytes(file: FileDto): Result<ByteArray> = Result.runCatching {
        File(getPath(file.user.id, file.fileHash)).readBytes()
    }

    suspend fun deleteFileById(fileId: UUID): Result<Unit> = Result.runCatching {
        val file = filePersistence.getFileById(fileId)

        if (file === null) {
            throw IllegalArgumentException("File with id $fileId not found")
        }

        // delete file from database
        filePersistence.deleteFileById(fileId)

        // delete file from disk
        deleteFileOnDiskSave(getPath(file.user.id, file.fileHash))
    }

    private fun getPath(userId: UUID, fileHash: String): String =
        "uploads/$userId/$fileHash"

    private fun createFileOnDiskSave(path: String, fileBytes: ByteArray) {
        val file = File(path)

        file.parentFile.mkdirs()
        file.writeBytes(fileBytes)
    }

    // delete file from disk if no file in database has the same path
    private suspend fun deleteFileOnDiskSave(path: String) {
        val userId = UUID.fromString(path.split("/")[1])
        val fileHash = path.split("/")[2]

        val isInDatabase = filePersistence.countFilesByUserIdAndHash(userId, fileHash) > 0

        if (isInDatabase) {
            return
        }

        File(path).delete()
    }
}

