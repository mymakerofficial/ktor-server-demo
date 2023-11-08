package de.maiker.mapper

import de.maiker.models.MediaFileDao
import de.maiker.models.MediaFileDto

fun MediaFileDao.toDto() = MediaFileDto(
    id = this.id.value,
    contentHash = this.contentHash,
    contentSize = this.contentSize,
    contentType = this.contentType,
    width = this.width,
    height = this.height,
)