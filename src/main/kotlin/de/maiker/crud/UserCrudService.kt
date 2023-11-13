package de.maiker.crud

import de.maiker.exceptions.UserAlreadyExistsException
import de.maiker.exceptions.UserNotFoundException
import de.maiker.exceptions.UsernameOrPasswordIncorrectException
import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserCrudService(
    private val userPersistence: UserPersistence = UserPersistence()
){
    suspend fun getAllUsers() = userPersistence.getAllUsers()

    suspend fun getUserById(userId: UUID): UserDto {
        val user = userPersistence.getUserById(userId)

        if (user === null) {
            throw UserNotFoundException(userId)
        }

        return user
    }

    suspend fun createUser(username: String, password: String): UserDto {
        val userExists = userPersistence.getUserByUsername(username) != null

        if (userExists) {
            throw UserAlreadyExistsException(username)
        }

        return userPersistence.createUser(username, password)
    }

    suspend fun getUserWithMatchingPassword(username: String, password: String): UserDto {
        val user = userPersistence.getUserByUsername(username)

        if (password != user?.password) {
            throw UsernameOrPasswordIncorrectException()
        }

        return user
    }

    suspend fun deleteUserById(id: UUID) = userPersistence.deleteUserById(id)
}


