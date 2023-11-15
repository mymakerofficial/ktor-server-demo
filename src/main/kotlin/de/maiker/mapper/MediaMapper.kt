package de.maiker.mapper

import de.maiker.models.*
import de.maiker.utils.getIfNotEmptyElse

fun MediaDao.toDto() = MediaDto(
    id = this.id.value,
    originalFileName = this.originalFileName,
    name = this.name,
    owner = this.owner.toDto(),
    files = this.files.toList().toDto(),
)

fun List<MediaDao>.toDto() = this.map { it.toDto() }

fun MediaDto.toSignedDto(signedFiles: List<MediaFileSignedDto>) = MediaSignedDto(
    id = this.id ?: throw IllegalStateException("MediaDto must have an id to be signed"),
    originalFileName = this.originalFileName,
    name = this.name,
    owner = this.owner,
    files = signedFiles,
)

fun MediaSignedDto.toListResponse() = MediaListResponse(
    id = this.id.toString(),
    name = this.name.getIfNotEmptyElse(this.originalFileName),
    files = this.files.toListResponse(),
)

fun List<MediaSignedDto>.toListResponse() = this.map { it.toListResponse() }

fun MediaSignedDto.toResponse() = MediaResponse(
    id = this.id.toString(),
    name = this.name ?: "",
    originalFileName = this.originalFileName,
    owner = this.owner.toResponse(),
    files = this.files.toListResponse(),
)