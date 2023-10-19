package de.maiker.persistence;

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.mapper.toDto
import de.maiker.models.FileDao
import de.maiker.models.FileDto
import de.maiker.models.Files
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import java.util.*

class FilePersistence {
    suspend fun getAllFilesByUserId(userId: UUID): List<FileDto> = dbQuery {
        Files.select {
            Files.userId eq userId
        }.mapNotNull {
            toDto(it)
        }
    }

    suspend fun getFileById(id: UUID): FileDto? = dbQuery {
        FileDao.findById(id)?.toDto()
    }

    suspend fun createFile(newUserId: UUID, newOriginalFileName: String, newFilePath: String, newFileSize: Int, newMimeType: String) = dbQuery {
        FileDao.new {
            originalFileName = newOriginalFileName
            filePath = newFilePath
            fileSize = newFileSize
            mimeType = newMimeType
            userId = newUserId
        }.toDto()
    }

    private fun toDto(row: ResultRow) = FileDto(
        id = row[Files.id].value,
        originalFileName = row[Files.originalFileName],
        filePath = row[Files.filePath],
        fileSize = row[Files.fileSize],
        mimeType = row[Files.mimeType],
        userId = row[Files.userId],
    )
}
