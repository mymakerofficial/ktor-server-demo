package de.maiker.service

import de.maiker.business.*
import de.maiker.models.MediaDto
import de.maiker.models.MediaFileDto
import io.ktor.http.*
import java.util.*

class ContentService(
    private val mediaService: MediaService = MediaService(),
    private val mediaFileService: MediaFileService = MediaFileService(),
    private val metadataReaderFactory: MetadataReaderFactory = MetadataReaderFactory(),
    private val previewGeneratorFactory: PreviewGeneratorFactory = PreviewGeneratorFactory()
) {
    private val previewResolution = 144

    // TODO: this should be handled by DI
    init {
        val imageScaler = ImageScaler()
        val frameExtractor = VideoFrameExtractor()

        val imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
        val videoPreviewGenerator = VideoPreviewGenerator(frameExtractor, imageScaler)

        previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Image.JPEG, ContentType.Image.PNG), imagePreviewGenerator)
        previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Video.MPEG, ContentType.Video.MP4), videoPreviewGenerator)
    }

    private fun getPreviewDimensions(width: Int, height: Int): Pair<Int, Int> {
        val imageAspectRatio = width.toDouble() / height.toDouble()
        val previewHeight = previewResolution
        val previewWidth = (previewHeight * imageAspectRatio).toInt()

        return previewWidth to previewHeight
    }

    suspend fun uploadMediaWithFile(userId: UUID, originalFileName: String, fileBytes: ByteArray, contentType: ContentType): MediaDto {
        val media = mediaService.createMedia(userId, originalFileName)
        check(media.id != null)

        val metadataReader = metadataReaderFactory.createMetadataReader(contentType)
        val (width, height) = metadataReader.getDimensions(fileBytes)
        val contentHash = fileBytes.contentHashCode().toString()

        val mediaFile = runCatching {
            mediaFileService.createMediaFile(
                MediaFileDto(
                    contentHash = contentHash,
                    contentSize = fileBytes.size,
                    contentType = contentType,
                    width = width,
                    height = height,
                    mediaId = media.id,
                ), fileBytes)
        }.getOrElse {
            mediaService.deleteMediaById(media.id)
            throw it
        }

        val (previewWidth, previewHeight) = getPreviewDimensions(width, height)

        val previewMediaFile = runCatching {
            generatePreviewForMediaFile(mediaFile, previewWidth, previewHeight)
        }.getOrElse {
            mediaService.deleteMediaById(media.id)
            throw it
        }

        return media.copy(
            files = listOf(mediaFile, previewMediaFile),
        )
    }

    private suspend fun generatePreviewForMediaFile(originalMediaFile: MediaFileDto, width: Int, height: Int): MediaFileDto {
        check(originalMediaFile.mediaId != null)

        val originalFileBytes = mediaFileService.readMediaFile(originalMediaFile)

        val previewGenerator = previewGeneratorFactory.createPreviewGenerator(originalMediaFile.contentType)
        val scaledFileBytes = previewGenerator.generate(originalFileBytes, width, height)

        val contentHash = scaledFileBytes.contentHashCode().toString()
        val contentSize = scaledFileBytes.size

        return mediaFileService.createMediaFile(
            MediaFileDto(
                contentHash = contentHash,
                contentSize = contentSize,
                contentType = ContentType.Image.JPEG,
                width = width,
                height = height,
                mediaId = originalMediaFile.mediaId,
            ), scaledFileBytes)
    }
}