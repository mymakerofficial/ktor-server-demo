package de.maiker.business

import io.ktor.http.*

interface PreviewGeneratorSpec {
    fun generate(bytes : ByteArray, width : Int, height : Int) : ByteArray
}

class ImagePreviewGenerator(
    val scaler: ImageScaler
) : PreviewGeneratorSpec {
    override fun generate(bytes: ByteArray, width: Int, height: Int)
        = scaler.scale(bytes, width, height)
}

class VideoPreviewGenerator : PreviewGeneratorSpec {
    override fun generate(bytes: ByteArray, width: Int, height: Int): ByteArray {
        TODO()
    }
}

class PreviewGeneratorFactory {
    private val generators = mutableMapOf<ContentType, PreviewGeneratorSpec>()

    fun registerPreviewGenerator(contentType: ContentType, previewGenerator: PreviewGeneratorSpec) {
        generators[contentType] = previewGenerator
    }

    fun registerPreviewGenerator(contentTypes: List<ContentType>, previewGenerator: PreviewGeneratorSpec) {
        contentTypes.forEach { contentType ->
            generators[contentType] = previewGenerator
        }
    }

    fun createPreviewGenerator(contentType: ContentType) : PreviewGeneratorSpec {
        return generators[contentType] ?: throw IllegalArgumentException("No preview generator for $contentType")
    }
}