package de.maiker.mapper

import de.maiker.models.FileDao
import de.maiker.models.FileDto
import de.maiker.models.FileResponse
import io.ktor.http.*

fun FileDao.toDto() = FileDto(
    id = this.id.value,
    originalFileName = this.originalFileName,
    filePath = this.filePath,
    fileSize = this.fileSize,
    mimeType = ContentType.parse(this.mimeType),
    userId = this.userId,
)

fun FileDto.toResponse() = FileResponse(
    id = this.id.toString(),
    name = this.originalFileName,
    size = this.fileSize,
    mimeType = this.mimeType.toString(),
)