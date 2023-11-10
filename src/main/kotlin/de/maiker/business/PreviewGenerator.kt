package de.maiker.business

import io.ktor.http.*

interface PreviewGeneratorSpec {
    fun generate(bytes : ByteArray, width : Int, height : Int) : ByteArray
}

class ImagePreviewGenerator : PreviewGeneratorSpec {
    val imageScaler = ImageScaler()

    override fun generate(bytes: ByteArray, width: Int, height: Int)
        = imageScaler.scale(bytes, width, height)
}

class PreviewGeneratorFactory {
    companion object {
        fun createPreviewGenerator(contentType: ContentType) : PreviewGeneratorSpec {
            if (contentType == ContentType.Image.Any)
                return ImagePreviewGenerator()

            throw Exception("Unsupported content type")
        }
    }
}