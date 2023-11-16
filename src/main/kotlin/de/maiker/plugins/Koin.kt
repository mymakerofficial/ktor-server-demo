package de.maiker.plugins

import de.maiker.business.*
import de.maiker.crud.MediaCrudService
import de.maiker.crud.MediaFileCrudService
import de.maiker.crud.UserCrudService
import de.maiker.persistence.MediaFilePersistence
import de.maiker.persistence.MediaPersistence
import de.maiker.persistence.UserPersistence
import de.maiker.service.MediaFileService
import de.maiker.service.MediaService
import de.maiker.service.UserService
import de.maiker.storage.StorageFactory
import io.ktor.http.*
import io.ktor.server.application.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

fun Application.configureKoin() {
    val appModule = module {
        single { MediaFilePersistence() }
        single { MediaPersistence()}
        single { UserPersistence() }

        single { MediaFileCrudService() }
        single { MediaCrudService() }
        single { UserCrudService() }

        single { MediaFileService() }
        single { MediaService() }
        single { UserService() }

        single { StorageFactory() }
        single { MetadataReaderFactory() }
        single {
            val factory = PreviewGeneratorFactory()

            val imageScaler = ImageScaler()
            val frameExtractor = VideoFrameExtractor()

            val imagePreviewGenerator = ImagePreviewGenerator(imageScaler)
            val videoPreviewGenerator = VideoPreviewGenerator(frameExtractor, imageScaler)

            factory.registerPreviewGenerator(listOf(ContentType.Image.JPEG, ContentType.Image.PNG), imagePreviewGenerator)
            factory.registerPreviewGenerator(listOf(ContentType.Video.MPEG, ContentType.Video.MP4), videoPreviewGenerator)

            factory
        }
    }

    startKoin {
        modules(appModule)
    }
}