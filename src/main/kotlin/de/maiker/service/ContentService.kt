package de.maiker.service

import de.maiker.business.*
import de.maiker.storage.StorageFactory
import de.maiker.crud.MediaCrudService
import de.maiker.crud.MediaFileCrudService
import de.maiker.crud.UserCrudService
import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDto
import io.ktor.http.*
import java.util.*

class ContentService(
    private val userCrudService: UserCrudService = UserCrudService(),
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
        val frameExtractor = VideoFrameExtractor()

        val imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
        val videoPreviewGenerator = VideoPreviewGenerator(frameExtractor, imageScaler)

        previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Image.JPEG, ContentType.Image.PNG), imagePreviewGenerator)
        previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Video.MPEG, ContentType.Video.MP4), videoPreviewGenerator)
    }

    private fun getPath(contentHash: String) = "$uploadsPath/$contentHash"

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
        val filePath = getPath(contentHash)

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
            storage.deleteFile(filePath)
            mediaFileCrudService.deleteMediaFileById(mediaFile.id)
            mediaCrudService.deleteMediaById(media.id)
            throw it
            // throw Exception("Failed to generate preview for media file")
        }

        media
    }

    suspend fun generatePreviewForMediaFile(originalMediaFile: MediaFileDto, width: Int, height: Int): Result<MediaFileDto> = Result.runCatching {
        val storage = storageFactory.createStorage()

        if (originalMediaFile.mediaId === null) {
            throw Exception("Media file does not belong to a media")
        }

        val originalFilePath = getPath(originalMediaFile.contentHash)
        val originalFileBytes = storage.readBytes(originalFilePath)

        val previewGenerator = previewGeneratorFactory.createPreviewGenerator(originalMediaFile.contentType)
        val scaledFileBytes = previewGenerator.generate(originalFileBytes, width, height)

        val contentHash = scaledFileBytes.contentHashCode().toString()
        val contentSize = scaledFileBytes.size
        val filePath = getPath(contentHash)

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
        val storage = storageFactory.createStorage()

        val tokenFileId = runCatching {
            authService.decode(token).getClaim(mediaFileClaim).asString()
        }.getOrElse {
            throw Exception("Invalid token")
        }

        val fileId = UUID.fromString(tokenFileId)

        val file = mediaFileCrudService.getMediaFileById(fileId).getOrThrow()

        val fileBytes = runCatching {
            storage.readBytes(getPath(file.contentHash))
        }.getOrElse {
            throw Exception("Failed to read file from disk")
        }

        file to fileBytes
    }

    suspend fun deleteUserCascadingById(userId: UUID) {
        deleteAllMediaCascadingByUserId(userId)
        userCrudService.deleteUserById(userId)
    }

    suspend fun deleteAllMediaCascadingByUserId(userId: UUID) {
        val media = mediaCrudService.getAllMediaByUserId(userId).getOrThrow()

        media.forEach {
            deleteAllMediaFilesByMediaId(it.id)
        }
    }

    suspend fun deleteMediaCascadingById(mediaId: UUID) {
        deleteAllMediaFilesByMediaId(mediaId)
        mediaCrudService.deleteMediaById(mediaId)
    }

    suspend fun deleteAllMediaFilesByMediaId(mediaId: UUID) {
        val files = mediaFileCrudService.getAllMediaFilesByMediaId(mediaId).getOrThrow()

        files.forEach {
            deleteMediaFileById(it.id)
        }
    }

    suspend fun deleteMediaFileById(fileId: UUID) {
        val storage = storageFactory.createStorage()

        val file = mediaFileCrudService.getMediaFileById(fileId).getOrThrow()

        storage.deleteFile(getPath(file.contentHash))
        mediaFileCrudService.deleteMediaFileById(fileId)
    }
}