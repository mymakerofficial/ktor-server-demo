package de.maiker.business

import io.ktor.http.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

interface MetadataReaderSpec {
    fun getDimensions(bytes: ByteArray) : Pair<Int, Int>

    fun readMetadata(bytes: ByteArray) : Map<String, String>
}

class ImageMetadataReader : MetadataReaderSpec {
    override fun getDimensions(bytes: ByteArray): Pair<Int, Int> {
        val originalImage: BufferedImage = ImageIO.read(ByteArrayInputStream(bytes))

        return Pair(originalImage.width, originalImage.height)
    }

    override fun readMetadata(bytes: ByteArray): Map<String, String> {
        return mapOf()
    }
}

class VideoMetadataReader : MetadataReaderSpec {
    override fun getDimensions(bytes: ByteArray): Pair<Int, Int> {
        return Pair(0, 0)
    }

    override fun readMetadata(bytes: ByteArray): Map<String, String> {
        return mapOf()
    }
}

class MetadataReaderFactory {
    fun createMetadataReader(contentType: ContentType) : MetadataReaderSpec {
        return when (contentType) {
            ContentType.Image.JPEG, ContentType.Image.PNG -> ImageMetadataReader()
            ContentType.Video.MPEG, ContentType.Video.MP4 -> VideoMetadataReader()
            else -> throw IllegalArgumentException("No metadata reader for $contentType")
        }
    }
}