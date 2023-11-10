package de.maiker.service

import de.maiker.business.*
import de.maiker.storage.StorageFactory
import de.maiker.crud.MediaCrudService
import de.maiker.crud.MediaFileCrudService
import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDto
import io.ktor.http.*
import java.io.File
import java.util.*

class ContentService(
    private val mediaCrudService: MediaCrudService = MediaCrudService(),
    private val mediaFileCrudService: MediaFileCrudService = MediaFileCrudService(),
    private val authService: AuthService = AuthService(),
    private val storageFactory: StorageFactory = StorageFactory(),
    private val metadataReaderFactory: MetadataReaderFactory = MetadataReaderFactory(),
    private val previewGeneratorFactory: PreviewGeneratorFactory = PreviewGeneratorFactory()
) {
    private val previewResolution = 144
    private val uploadsPath = "uploads"
    private val mediaFileClaim = "fid"

    // TODO: this should be handled by DI
    init {
        val imageScaler = ImageScaler()

        val imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
        val videoPreviewGenerator = VideoPreviewGenerator()

        previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Image.JPEG, ContentType.Image.PNG), imagePreviewGenerator)
        previewGeneratorFactory.registerPreviewGenerator(ContentType.Video.MPEG, videoPreviewGenerator)
    }

    private fun getPreviewDimensions(width: Int, height: Int): Pair<Int, Int> {
        val imageAspectRatio = width.toDouble() / height.toDouble()
        val previewHeight = previewResolution
        val previewWidth = (previewHeight * imageAspectRatio).toInt()

        return previewWidth to previewHeight
    }

    suspend fun uploadMediaWithFile(userId: UUID, originalFileName: String, fileBytes: ByteArray, contentType: ContentType): Result<MediaDto> = Result.runCatching {
        val storage = storageFactory.createStorage()

        val media = mediaCrudService.createMedia(userId, originalFileName).getOrThrow()

        val contentHash = fileBytes.contentHashCode().toString()
        val filePath = "$uploadsPath/$contentHash"

        runCatching {
            storage.writeBytes(filePath, fileBytes)
        }.onFailure {
            mediaCrudService.deleteMediaById(media.id)
            throw Exception("Failed to save file to disk, media was not created")
        }

        val metadataReader = metadataReaderFactory.createMetadataReader(contentType)
        val (width, height) = metadataReader.getDimensions(fileBytes)

        val mediaFile = mediaFileCrudService.createMediaFile(
            mediaId = media.id,
            contentHash,
            contentSize = fileBytes.size,
            contentType = contentType.toString(),
            width,
            height
        ).getOrElse {
            storage.deleteFile(filePath)
            mediaCrudService.deleteMediaById(media.id)
            throw Exception("Failed to create media file entry, media was not created")
        }

        val (previewWidth, previewHeight) = getPreviewDimensions(width, height)

        generatePreviewForMediaFile(mediaFile, previewWidth, previewHeight).onFailure {
            throw Exception("Failed to generate preview for media file")
        }

        media
    }

    suspend fun generatePreviewForMediaFile(originalMediaFile: MediaFileDto, width: Int, height: Int): Result<MediaFileDto> = Result.runCatching {
        val storage = storageFactory.createStorage()

        if (originalMediaFile.mediaId === null) {
            throw Exception("Media file does not belong to a media")
        }

        val originalFilePath = "$uploadsPath/${originalMediaFile.contentHash}"
        val originalFileBytes = storage.readBytes(originalFilePath)

        val previewGenerator = previewGeneratorFactory.createPreviewGenerator(originalMediaFile.contentType)
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