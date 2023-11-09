package de.maiker.mapper

import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDao
import de.maiker.models.MediaFileDto
import de.maiker.models.MediaFileListResponse

fun MediaFileDao.toDto() = MediaFileDto(
    id = this.id.value,
    contentHash = this.contentHash,
    contentSize = this.contentSize,
    contentType = this.contentType,
    width = this.width,
    height = this.height,
)

fun List<MediaFileDao>.toDto() = this.map { it.toDto() }

fun MediaFileDto.toListResponse() = MediaFileListResponse(
    id = this.id.toString(),
    contentSize = this.contentSize,
    contentType = this.contentType,
    width = this.width,
    height = this.height,
)

fun List<MediaFileDto>.toListResponse() = this.map { it.toListResponse() }