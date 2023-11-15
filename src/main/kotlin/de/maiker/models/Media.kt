package de.maiker.models

import io.github.smiley4.ktorswaggerui.dsl.Example
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Media: UUIDTable("media") {
    val originalFileName = varchar("originalfilename", 255)
    val name = varchar("name", 255).nullable()
    val ownerId = reference("ownerid", Users)
}

class MediaDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MediaDao>(Media)

    var originalFileName by Media.originalFileName
    var name by Media.name
    var owner by UserDao referencedOn Media.ownerId
    val files by MediaFileDao optionalReferrersOn MediaFiles.mediaId
}

data class MediaDto(
    val id: UUID? = null,
    val originalFileName: String,
    val name: String?,
    val owner: UserDto,
    val files: List<MediaFileDto>,
)

data class MediaSignedDto(
    val id: UUID,
    val originalFileName: String,
    val name: String?,
    val owner: UserDto,
    val files: List<MediaFileSignedDto>,
)

@Serializable
data class MediaListResponse(
    @Example("1531068e-6210-46ac-ac74-38fbc1ed0fc7")
    val id: String,
    @Example("IMG_20210101_123456.jpg")
    val name: String,
    val files: List<MediaFileListResponse>,
)

@Serializable
data class MediaResponse (
    @Example("1531068e-6210-46ac-ac74-38fbc1ed0fc7")
    val id: String,
    val name: String,
    @Example("IMG_20210101_123456.jpg")
    val originalFileName: String,
    val owner: UserResponse,
    val files: List<MediaFileListResponse>,
)