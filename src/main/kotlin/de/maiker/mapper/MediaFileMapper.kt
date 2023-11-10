package de.maiker.mapper

import de.maiker.models.MediaFileDao
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaFileListResponse
import de.maiker.service.AuthService
import io.ktor.http.*

val authService = AuthService() // nooooooooooooooo

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

fun MediaFileDto.toListResponse() = MediaFileListResponse(
    id = this.id.toString(),
    contentSize = this.contentSize,
    contentType = this.contentType.contentType,
    contentSubtype = this.contentType.contentSubtype,
    width = this.width,
    height = this.height,
    // this should not be here
    url = "/api/file/${authService.sign("fid", this.id.toString())}/raw",
)

fun List<MediaFileDto>.toListResponse() = this.map { it.toListResponse() }