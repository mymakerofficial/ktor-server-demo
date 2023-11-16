package de.maiker.service

import de.maiker.crud.UserCrudService
import de.maiker.exceptions.UsernameOrPasswordIncorrectException
import de.maiker.models.UserDto
import java.util.*

class UserService(
    private val crudService: UserCrudService,
    private val mediaService: MediaService,
) {
    suspend fun getAllUsers() = crudService.getAllUsers()

    suspend fun getUserById(userId: UUID) = crudService.getUserById(userId)

    suspend fun getUserWithMatchingPassword(username: String, password: String): UserDto {
        val user = crudService.getUserByUsername(username)

        if (password != user.password) {
            throw UsernameOrPasswordIncorrectException()
        }

        return user
    }

    suspend fun createUser(username: String, password: String) = crudService.createUser(username, password)

    suspend fun deleteUserById(id: UUID) {
        val media = mediaService.getAllMediaByUserId(id)
        media.forEach {
            check(it.id != null)
            mediaService.deleteMediaById(it.id)
        }
        crudService.deleteUserById(id)
    }
}