package de.maiker.crud

import de.maiker.exceptions.UserAlreadyExistsException
import de.maiker.exceptions.UserNotFoundException
import de.maiker.exceptions.UsernameOrPasswordIncorrectException
import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserCrudService{
    private val userPersistence = UserPersistence()

    suspend fun getAllUsers(): Result<List<UserDto>> = Result.runCatching {
        userPersistence.getAllUsers()
    }

    suspend fun getUserById(userId: UUID): Result<UserDto> = Result.runCatching {
        val user = userPersistence.getUserById(userId)

        if (user === null) {
            throw UserNotFoundException(userId)
        }

        user
    }

    suspend fun createUser(username: String, password: String): Result<UserDto> = Result.runCatching {
        val userExists = userPersistence.getUserByUsername(username) != null

        if (userExists) {
            throw UserAlreadyExistsException(username)
        }

        userPersistence.createUser(username, password)
    }

    suspend fun getUserWithMatchingPassword(username: String, password: String): Result<UserDto> = Result.runCatching {
        val user = userPersistence.getUserByUsername(username)

        if (password != user?.password) {
            throw UsernameOrPasswordIncorrectException()
        }

        user
    }

    suspend fun deleteUserById(id: UUID) {
        userPersistence.deleteUserById(id)
    }
}


