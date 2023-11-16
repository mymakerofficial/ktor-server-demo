package de.maiker.service

import de.maiker.models.UserDto

class UserAuthService(
    private val userService: UserService,
    private val authService: AuthService,
) {
    suspend fun authenticate(username: String, password: String): Pair<String, UserDto> {
        val user = userService.getUserWithMatchingPassword(username, password)

        val token = authService.sign("user_id", user.id.toString())

        return token to user
    }
}