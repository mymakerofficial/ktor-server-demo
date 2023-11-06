package de.maiker.models

import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Files: UUIDTable("files") {
    val originalFileName = varchar("originalfilename", 255)
    val fileHash = varchar("filehash", 255)
    val fileSize = integer("filesize")
    val mimeType = varchar("mimetype", 255)
    val userId = reference("userid", Users)
}

class FileDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<FileDao>(Files)

    var originalFileName by Files.originalFileName
    var fileHash by Files.fileHash
    var fileSize by Files.fileSize
    var mimeType by Files.mimeType
    var user by UserDao referencedOn Files.userId
}

data class FileDto(
    val id: UUID,
    val originalFileName: String,
    val fileHash: String,
    val fileSize: Int,
    val mimeType: ContentType,
    val user: UserDto,
)

@Serializable
data class ListFileResponse(
    val id: String,
    val name: String,
)

@Serializable
data class ListFileResponseWithUrl(
    val id: String,
    val name: String,
    val url: String,
)

@Serializable
data class FileResponse (
    val id: String,
    val name: String,
    val size: Int,
    val mimeType: String,
    val owner: UserResponse,
    val url: String,
)