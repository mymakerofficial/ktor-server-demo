package de.maiker.service

import de.maiker.models.FileDto
import de.maiker.persistence.FilePersistence
import io.ktor.http.*
import java.io.File
import java.util.*
class FileService {
    private val filePersistence = FilePersistence()

    suspend fun getAllFilesByUserId(userId: UUID): Result<List<FileDto>> =
        Result.runCatching { filePersistence.getAllFilesByUserId(userId) }

    suspend fun createFile(userId: UUID, originalFileName: String, mimeType: ContentType, fileBytes: ByteArray): Result<FileDto> {
        val fileHash = fileBytes.contentHashCode().toString()
        val filePath = getPath(userId, fileHash)
        val fileSize = fileBytes.size

        // save file to disk
        runCatching {
            createFileOnDiskSave(filePath, fileBytes)
        }.onFailure {
            return Result.failure(it)
        }

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
            return Result.failure(it)
        }

        return Result.success(file)
    }

    suspend fun getFileById(fileId: UUID): Result<FileDto> {
        val file = filePersistence.getFileById(fileId)

        return if (file != null) {
            Result.success(file)
        } else {
            Result.failure(IllegalArgumentException("File with id $fileId not found"))
        }
    }

    suspend fun getFileByIdAndUserId(fileId: UUID, userId: UUID): Result<FileDto> {
        val file = getFileById(fileId).getOrElse { return Result.failure(it) }

        if (file.user.id != userId) {
            return Result.failure(IllegalArgumentException("File with id $fileId not found"))
        }

        return Result.success(file)
    }

    fun readFileBytes(file: FileDto): Result<ByteArray> =
        Result.runCatching { File(getPath(file.user.id, file.fileHash)).readBytes() }

    suspend fun deleteFileById(fileId: UUID): Result<Unit> {
        val file = filePersistence.getFileById(fileId)
            ?: return Result.failure(IllegalArgumentException("File with id $fileId not found"))

        // delete file from database
        runCatching {
            filePersistence.deleteFileById(fileId)
        }.onFailure {
            return Result.failure(it)
        }

        // delete file from disk
        runCatching {
            deleteFileOnDiskSave(getPath(file.user.id, file.fileHash))
        }.onFailure {
            return Result.failure(it)
        }

        return Result.success(Unit)
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

