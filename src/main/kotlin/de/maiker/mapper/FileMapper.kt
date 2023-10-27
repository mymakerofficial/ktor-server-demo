package de.maiker.mapper

import de.maiker.models.FileDao
import de.maiker.models.FileDto
import de.maiker.models.FileResponse
import de.maiker.models.FileResponseAuthenticated
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

fun FileResponse.withUrl(url: String) = FileResponseAuthenticated(
    id = this.id,
    name = this.name,
    size = this.size,
    mimeType = this.mimeType,
    url = url,
)