package de.maiker.mapper

import de.maiker.models.MediaFileDao
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaFileListResponse
import de.maiker.models.MediaFileSignedDto
import de.maiker.service.AuthService
import io.ktor.http.*

fun MediaFileDao.toDto() = MediaFileDto(
    id = this.id.value,
    contentHash = this.contentHash,
    contentSize = this.contentSize,
    contentType = ContentType.parse(this.contentType),
    width = this.width,
    height = this.height,
    mediaId = this.media?.id?.value,
)

fun List<MediaFileDao>.toDto() = this.map { it.toDto() }

fun MediaFileDto.toSignedDto(token: String) = MediaFileSignedDto(
    id = this.id ?: throw IllegalStateException("MediaFileDto must have an id to be signed"),
    contentHash = this.contentHash,
    contentSize = this.contentSize,
    contentType = this.contentType,
    width = this.width,
    height = this.height,
    mediaId = this.mediaId,
    token = token,
)

fun MediaFileSignedDto.toListResponse() = MediaFileListResponse(
    id = this.id.toString(),
    contentSize = this.contentSize,
    contentType = this.contentType.contentType,
    contentSubtype = this.contentType.contentSubtype,
    width = this.width,
    height = this.height,
    url = "/api/file/${this.token}/raw",
)

fun List<MediaFileSignedDto>.toListResponse() = this.map { it.toListResponse() }