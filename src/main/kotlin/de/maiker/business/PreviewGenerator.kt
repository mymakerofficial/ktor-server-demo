package de.maiker.business

import io.ktor.http.*

interface PreviewGeneratorSpec {
    fun generate(bytes : ByteArray, width : Int, height : Int) : ByteArray
}

class ImagePreviewGenerator(
    contentType: ContentType
) : PreviewGeneratorSpec {
    private val scaler = ImageScalerFactory.createImageScaler(contentType)

    override fun generate(bytes: ByteArray, width: Int, height: Int)
        = scaler.scale(bytes, width, height)
}

class PreviewGeneratorFactory {
    companion object {
        fun createPreviewGenerator(contentType: ContentType) : PreviewGeneratorSpec {
            if (contentType == ContentType.Image.Any)
                return ImagePreviewGenerator(contentType)

            throw Exception("Unsupported content type")
        }
    }
}