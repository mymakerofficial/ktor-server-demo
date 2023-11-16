package de.maiker.routes

import de.maiker.service.*
import io.ktor.server.routing.*

fun Route.apiRouting(
    userService: UserService,
    userAuthService: UserAuthService,
    mediaService: MediaService,
    mediaFileService: MediaFileService,
    contentService: ContentService,
) {
    route("/api") {
        authRouting(
            userService,
            userAuthService,
        )
        userRouting(
            userService,
        )
        mediaRouting(
            mediaService,
            contentService,
        )
        fileRouting(
            mediaFileService,
        )
    }
}