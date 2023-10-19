package de.maiker.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Files: UUIDTable("files") {
    val originalFileName = varchar("originalfilename", 255)
    val filePath = varchar("filepath", 255)
    val fileSize = integer("filesize")
    val mimeType = varchar("mimetype", 255)
    val userId = uuid("userid").references(Users.id)
}

class FileDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<FileDao>(Files)

    var originalFileName by Files.originalFileName
    var filePath by Files.filePath
    var fileSize by Files.fileSize
    var mimeType by Files.mimeType
    var userId by Files.userId
}

data class FileDto(
    val id: UUID,
    val originalFileName: String,
    val filePath: String,
    val fileSize: Int,
    val mimeType: String,
    val userId: UUID,
)

@Serializable
data class FileResponse(
    val id: String,
    val name: String,
    val size: Int,
    val mimeType: String,
)