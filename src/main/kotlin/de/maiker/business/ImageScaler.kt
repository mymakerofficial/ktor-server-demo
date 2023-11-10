package de.maiker.business

import io.ktor.http.*
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


interface ImageScalerSpec {
    fun scale(bytes : ByteArray, width : Int, height : Int) : ByteArray
}

class ImageScaler : ImageScalerSpec {
    override fun scale(bytes: ByteArray, width: Int, height: Int): ByteArray {
        val originalImage: BufferedImage = ImageIO.read(ByteArrayInputStream(bytes))

        val scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH)

        val imageBuffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        imageBuffer.graphics.drawImage(scaledImage, 0, 0, null)

        val baos = ByteArrayOutputStream()
        ImageIO.write(imageBuffer, "jpg", baos)
        return baos.toByteArray()
    }
}

class ImageScalerFactory {
    companion object {
        fun createImageScaler(contentType: ContentType) : ImageScalerSpec {
            if (contentType == ContentType.Image.Any)
                return ImageScaler()

            throw Exception("Unsupported content type")
        }
    }
}