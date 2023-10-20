package de.maiker.service

import de.maiker.models.FileDto
import de.maiker.persistence.FilePersistence
import io.ktor.http.*
import java.io.File
import java.util.*

class FileService {
    private val filePersistence = FilePersistence()

    suspend fun getAllFilesByUserId(userId: UUID) =
        filePersistence.getAllFilesByUserId(userId)

    suspend fun createFile(userId: UUID, originalFileName: String, mimeType: ContentType, fileBytes: ByteArray): FileDto {
        val fileHash = fileBytes.hashCode()
        val filePath = "uploads/$fileHash"
        val fileSize = fileBytes.size

        File(filePath).writeBytes(fileBytes)

        return filePersistence.createFile(
            originalFileName = originalFileName,
            filePath = filePath,
            fileSize = fileSize,
            mimeType = mimeType,
            userId = userId,
        )
    }

    suspend fun getFileById(fileId: UUID) =
        filePersistence.getFileById(fileId)

    suspend fun getFileByIdAndUserId(fileId: UUID, userId: UUID): FileDto? {
        val file = getFileById(fileId)

        if (file?.userId != userId) {
            return null
        }

        return file
    }

    fun readFile(file: FileDto): ByteArray =
        File(file.filePath).readBytes()
}