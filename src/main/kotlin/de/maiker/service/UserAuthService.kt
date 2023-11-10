package de.maiker.service

import de.maiker.models.UserDto

class UserAuthService {
    val userService = UserService()
    val authService = AuthService()

    suspend fun authenticate(username: String, password: String): Result<Pair<String, UserDto>> = Result.runCatching {
        val user = userService.getUserWithMatchingPassword(username, password).getOrThrow()

        val token = authService.sign("user_id", user.id.toString())

        token to user
    }
}