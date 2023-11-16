package de.maiker

import de.maiker.business.*
import de.maiker.crud.MediaCrudService
import de.maiker.crud.MediaFileCrudService
import de.maiker.crud.UserCrudService
import de.maiker.persistence.MediaFilePersistence
import de.maiker.persistence.MediaPersistence
import de.maiker.persistence.UserPersistence
import de.maiker.plugins.*
import de.maiker.routes.apiRouting
import de.maiker.service.*
import de.maiker.storage.JStorage
import de.maiker.storage.StorageFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    configureSecurity()
    configureCORS()
    configureOpenApi()
    configureSerialization()
    configureStatus()

    connectDatabase(
        dbUrl = "jdbc:postgresql://localhost:5432/postgres",
        dbUser = "postgres",
        dbPassword = "postgres",
    )

    val authService = AuthService(
        secret = "secret",
    )

    val mediaFileCrudService = MediaFileCrudService(
        persistence = MediaFilePersistence(),
    )
    val mediaCrudService = MediaCrudService(
        persistence = MediaPersistence(),
    )
    val userCrudService = UserCrudService(
        persistence = UserPersistence(),
    )

    val metadataReaderFactory = MetadataReaderFactory()

    val previewGeneratorFactory = PreviewGeneratorFactory()

    val imageScaler = ImageScaler()
    val frameExtractor = VideoFrameExtractor()

    val imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
    val videoPreviewGenerator = VideoPreviewGenerator(frameExtractor, imageScaler)

    previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Image.JPEG, ContentType.Image.PNG), imagePreviewGenerator)
    previewGeneratorFactory.registerPreviewGenerator(listOf(ContentType.Video.MPEG, ContentType.Video.MP4), videoPreviewGenerator)

    val storageFactory = StorageFactory()

    val mediaFileService = MediaFileService(
        crudService = mediaFileCrudService,
        authService,
        storageFactory,
    )
    val mediaService = MediaService(
        crudService = mediaCrudService,
        mediaFileService
    )
    val userService = UserService(
        crudService = userCrudService,
        mediaService,
    )
    val userAuthService = UserAuthService(
        userService,
        authService,
    )
    val contentService = ContentService(
        mediaService,
        mediaFileService,
        metadataReaderFactory,
        previewGeneratorFactory,
    )

    routing {
        apiRouting(
            userService,
            userAuthService,
            mediaService,
            mediaFileService,
            contentService,
        )
    }
}
