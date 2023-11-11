package de.maiker.business

import io.ktor.http.*
import org.bytedeco.javacv.FFmpegFrameGrabber
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
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
        val tempFile = File.createTempFile("video", ".mp4").apply {
            writeBytes(bytes)
            deleteOnExit()
        }
        val grabber = FFmpegFrameGrabber(tempFile)
        grabber.start()

        val width = grabber.imageWidth
        val height = grabber.imageHeight

        grabber.stop()

        return Pair(width, height)
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