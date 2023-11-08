package de.maiker.mapper

import de.maiker.models.*

fun MediaDao.toDto() = MediaDto(
    id = this.id.value,
    originalFileName = this.originalFileName,
    name = this.name,
    owner = this.owner.toDto(),
    files = this.files.map { it.toDto() },
)

fun MediaDto.toListResponse() = MediaListResponse(
    id = this.id.toString(),
    name = this.name ?: this.originalFileName,
)

fun List<MediaDto>.toListResponse() = this.map { it.toListResponse() }

fun MediaDto.toResponse() = MediaResponse(
    id = this.id.toString(),
    name = this.name ?: "",
    originalFileName = this.originalFileName,
    owner = this.owner.toResponse(),
)