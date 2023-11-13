package de.maiker.service

import de.maiker.crud.UserCrudService
import de.maiker.models.UserDto

class UserAuthService(
    private val userCrudService: UserCrudService = UserCrudService(),
    private val authService: AuthService = AuthService()
) {
    suspend fun authenticate(username: String, password: String): Pair<String, UserDto> {
        val user = userCrudService.getUserWithMatchingPassword(username, password).getOrThrow()

        val token = authService.sign("user_id", user.id.toString())

        return token to user
    }
}