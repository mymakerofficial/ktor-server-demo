package de.maiker.service

import de.maiker.models.FileDto
import de.maiker.persistence.FilePersistence
import java.io.File
import java.util.*

class FileService {
    private val filePersistence = FilePersistence()

    suspend fun getAllFilesByUserId(userId: UUID) =
        filePersistence.getAllFilesByUserId(userId)

    suspend fun createFile(userId: UUID, originalFileName: String, mimeType: String, fileBytes: ByteArray): FileDto {
        val fileHash = fileBytes.hashCode()
        val filePath = "uploads/$fileHash"
        val fileSize = fileBytes.size

        File(filePath).writeBytes(fileBytes)

        return filePersistence.createFile(userId, originalFileName, filePath, fileSize, mimeType)
    }

    suspend fun getFileById(fileId: UUID) =
        filePersistence.getFileById(fileId)

    fun readFile(file: FileDto): ByteArray =
        File(file.filePath).readBytes()
}