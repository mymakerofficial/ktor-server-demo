package de.maiker.business

import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO


interface VideoFrameExtractorSpec {
    fun extractFrame(bytes: ByteArray, frameNumber: Int): ByteArray
}

class VideoFrameExtractor : VideoFrameExtractorSpec {
    override fun extractFrame(bytes: ByteArray, frameNumber: Int): ByteArray {
        // convert byte array back to File cause FFmpegFrameGrabber can't read from byte array
        // also we use FFmpegFrameGrabber cause it's the only one that will actually accept a File and not a path
        val tempFile = File.createTempFile("video", ".mp4").apply {
            writeBytes(bytes)
            deleteOnExit()
        }

        val grabber = FFmpegFrameGrabber(tempFile)
        grabber.start()
        grabber.setVideoFrameNumber(frameNumber)

        // use grabImage() instead of grab() so we actually get an image
        with(grabber.grabImage()) {
            // convert the frame to a buffered image, this is needed to actually get the image data in a format we can use
            val converter = Java2DFrameConverter()
            val bufferedImage: BufferedImage = converter.convert(this)

            // our next step to make useful data
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "jpeg", outputStream)

            return outputStream.toByteArray()
        }
    }
}