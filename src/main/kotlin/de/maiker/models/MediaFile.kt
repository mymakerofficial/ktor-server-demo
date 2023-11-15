package de.maiker.models

import io.github.smiley4.ktorswaggerui.dsl.Example
import io.ktor.http.*
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
    val id: UUID? = null,
    val contentHash: String,
    val contentSize: Int,
    val contentType: ContentType,
    val width: Int?,
    val height: Int?,
    val mediaId: UUID?,
)

data class MediaFileSignedDto(
    val id: UUID,
    val contentHash: String,
    val contentSize: Int,
    val contentType: ContentType,
    val width: Int?,
    val height: Int?,
    val mediaId: UUID?,
    val token: String,
)

@Serializable
data class MediaFileListResponse(
    @Example("1531068e-6210-46ac-ac74-38fbc1ed0fc7")
    val id: String,
    @Example("32521")
    val contentSize: Int,
    @Example("image")
    val contentType: String,
    @Example("jpeg")
    val contentSubtype: String,
    @Example("1920")
    val width: Int?,
    @Example("1080")
    val height: Int?,
    val url: String? = null,
)