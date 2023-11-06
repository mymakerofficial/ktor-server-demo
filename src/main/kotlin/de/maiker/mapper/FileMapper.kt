package de.maiker.mapper

import de.maiker.models.*
import io.ktor.http.*

fun FileDao.toDto() = FileDto(
    id = this.id.value,
    originalFileName = this.originalFileName,
    fileHash = this.fileHash,
    fileSize = this.fileSize,
    mimeType = ContentType.parse(this.mimeType),
    user = this.user.toDto(),
)

fun FileDto.toListResponse() = ListFileResponse(
    id = this.id.toString(),
    name = this.originalFileName,
)

fun List<FileDto>.toListResponse() = this.map { it.toListResponse() }

fun ListFileResponse.withUrl(url: String) = ListFileResponseWithUrl(
    id = this.id,
    name = this.name,
    url = url,
)

fun List<ListFileResponse>.withUrl(url: String) = this.map { it.withUrl(url) }

fun FileDto.toResponse(url: String) = FileResponse(
    id = this.id.toString(),
    name = this.originalFileName,
    size = this.fileSize,
    mimeType = this.mimeType.toString(),
    owner = this.user.toResponse(),
    url = url,
)