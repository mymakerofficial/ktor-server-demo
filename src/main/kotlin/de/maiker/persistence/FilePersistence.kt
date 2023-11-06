package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.mapper.toDto
import de.maiker.models.*
import io.ktor.http.*
import java.util.*

class FilePersistence {
    suspend fun getAllFilesByUserId(userId: UUID): List<FileDto> = dbQuery {
        FileDao.find { Files.userId eq userId }.toList().map { it.toDto() }
    }

    suspend fun getFileById(id: UUID): FileDto? = dbQuery {
        FileDao.findById(id)?.toDto()
    }

    suspend fun countFilesByPath(path: String): Long = dbQuery {
        FileDao.find { Files.filePath eq path }.count()
    }

    suspend fun createFile(userId: UUID, originalFileName: String, filePath: String, fileSize: Int, mimeType: ContentType) = dbQuery {
        val user = UserDao.findById(userId) ?: throw IllegalArgumentException("User with id $userId not found")

        FileDao.new {
            this.originalFileName = originalFileName
            this.filePath = filePath
            this.fileSize = fileSize
            this.mimeType = mimeType.toString()
            this.user = user
        }.toDto()
    }

    suspend fun deleteFileById(fileId: UUID) = dbQuery {
        FileDao.findById(fileId)?.delete()
    }
}
