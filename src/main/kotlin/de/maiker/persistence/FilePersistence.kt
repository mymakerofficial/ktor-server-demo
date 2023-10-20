package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.mapper.toDto
import de.maiker.models.FileDao
import de.maiker.models.FileDto
import io.ktor.http.*
import java.util.*

class FilePersistence {
    suspend fun getAllFilesByUserId(userId: UUID): List<FileDto> = dbQuery {
        FileDao.findAllByUserId(userId).map { it.toDto() }
    }

    suspend fun getFileById(id: UUID): FileDto? = dbQuery {
        FileDao.findById(id)?.toDto()
    }

    suspend fun createFile(userId: UUID, originalFileName: String, filePath: String, fileSize: Int, mimeType: ContentType) = dbQuery {
        FileDao.new {
            this.originalFileName = originalFileName
            this.filePath = filePath
            this.fileSize = fileSize
            this.mimeType = mimeType.toString()
            this.userId = userId
        }.toDto()
    }
}
