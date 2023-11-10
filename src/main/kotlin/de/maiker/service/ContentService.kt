package de.maiker.service

import de.maiker.business.MetadataReaderFactory
import de.maiker.business.PreviewGeneratorFactory
import de.maiker.storage.StorageFactory
import de.maiker.crud.MediaCrudService
import de.maiker.crud.MediaFileCrudService
import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDto
import io.ktor.http.*
import java.io.File
import java.util.*

class ContentService {
    private val mediaCrudService = MediaCrudService()
    private val mediaFileCrudService = MediaFileCrudService()
    private val authService = AuthService()
    private val storage = StorageFactory.createStorage()

    private val thumbnailResolution = 144

    private val uploadsPath = "uploads"
    private val mediaFileClaim = "fid"

    suspend fun uploadMediaWithFile(userId: UUID, originalFileName: String, fileBytes: ByteArray, contentType: ContentType): Result<MediaDto> = Result.runCatching {
        val metadataReader = MetadataReaderFactory.createMetadataReader(contentType)

        val contentHash = fileBytes.contentHashCode().toString()
        val contentSize = fileBytes.size
        val filePath = "$uploadsPath/$contentHash"
        val (width, height) = metadataReader.getDimensions(fileBytes)
        val imageAspectRatio = width.toDouble() / height.toDouble()
        val thumbnailHeight = thumbnailResolution
        val thumbnailWidth = (thumbnailHeight * imageAspectRatio).toInt()

        val media = mediaCrudService.createMedia(userId, originalFileName).getOrThrow()

        runCatching {
            storage.writeBytes(filePath, fileBytes)
        }.onFailure {
            mediaCrudService.deleteMediaById(media.id)
            throw Exception("Failed to save file to disk, media was not created")
        }

        val mediaFile = mediaFileCrudService.createMediaFile(
            mediaId = media.id,
            contentHash,
            contentSize,
            contentType = contentType.toString(),
            width,
            height
        ).getOrElse {
            storage.deleteFile(filePath)
            mediaCrudService.deleteMediaById(media.id)
            throw Exception("Failed to create media file entry, media was not created")
        }

        generatePreviewForMediaFile(mediaFile, thumbnailWidth, thumbnailHeight).onFailure {
            throw Exception("Failed to generate preview for media file")
        }

        media
    }

    suspend fun generatePreviewForMediaFile(originalMediaFile: MediaFileDto, width: Int, height: Int): Result<MediaFileDto> = Result.runCatching {
        if (originalMediaFile.mediaId === null) {
            throw Exception("Media file does not belong to a media")
        }

        val originalFilePath = "$uploadsPath/${originalMediaFile.contentHash}"
        val originalFileBytes = storage.readBytes(originalFilePath)

        val previewGenerator = PreviewGeneratorFactory.createPreviewGenerator(originalMediaFile.contentType)
        val scaledFileBytes = previewGenerator.generate(originalFileBytes, width, height)

        val contentHash = scaledFileBytes.contentHashCode().toString()
        val contentSize = scaledFileBytes.size
        val filePath = "$uploadsPath/$contentHash"

        storage.writeBytes(filePath, scaledFileBytes)

        mediaFileCrudService.createMediaFile(
            mediaId = originalMediaFile.mediaId,
            contentHash,
            contentSize,
            contentType = ContentType.Image.JPEG.toString(),
            width,
            height
        ).getOrElse {
            storage.deleteFile(filePath)
            throw it
        }
    }

    suspend fun getFileWithAuthentication(token: String): Result<Pair<MediaFileDto, ByteArray>> = Result.runCatching {
        val tokenFileId = runCatching {
            authService.decode(token).getClaim(mediaFileClaim).asString()
        }.getOrElse {
            throw Exception("Invalid token")
        }

        val fileId = UUID.fromString(tokenFileId)

        val file = mediaFileCrudService.getMediaFileById(fileId).getOrThrow()

        val fileBytes = runCatching {
            File("$uploadsPath/${file.contentHash}").readBytes()
        }.getOrElse {
            throw Exception("Failed to read file from disk")
        }

        file to fileBytes
    }
}