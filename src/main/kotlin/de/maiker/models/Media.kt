package de.maiker.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Media: UUIDTable("files") {
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
    val id: UUID,
    val originalFileName: String,
    val name: String?,
    val owner: UserDto,
    val files: List<MediaFileDto>,
)

@Serializable
data class MediaListResponse(
    val id: String,
    val name: String,
)

@Serializable
data class MediaResponse (
    val id: String,
    val name: String,
    val originalFileName: String,
    val owner: UserResponse,
)