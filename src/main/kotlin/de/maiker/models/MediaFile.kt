package de.maiker.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object MediaFiles: UUIDTable("media_files") {
    val mediaId = reference("mediaid", Media).nullable()
    val contentHash = varchar("contenthash", 255)
    val contentSize = integer("contentsize")
    val contentType = varchar("contenttype", 255)
    val width = integer("width").nullable()
    val height = integer("height").nullable()
}

class MediaFileDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MediaFileDao>(MediaFiles)

    var media by MediaDao optionalReferencedOn MediaFiles.mediaId
    var contentHash by MediaFiles.contentHash
    var contentSize by MediaFiles.contentSize
    var contentType by MediaFiles.contentType
    var width by MediaFiles.width
    var height by MediaFiles.height
}

data class MediaFileDto(
    val id: UUID,
    val contentHash: String,
    val contentSize: Int,
    val contentType: String,
    val width: Int?,
    val height: Int?,
)

@Serializable
data class MediaFileListResponse(
    val id: String,
    val contentSize: Int,
    val contentType: String,
    val width: Int?,
    val height: Int?,
)