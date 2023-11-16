package de.maiker.crud

import de.maiker.exceptions.UserAlreadyExistsException
import de.maiker.exceptions.UserNotFoundException
import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class UserCrudService : KoinComponent {
    private val persistence: UserPersistence by inject()

    suspend fun getAllUsers() = persistence.getAllUsers()

    suspend fun getUserById(userId: UUID): UserDto {
        val user = persistence.getUserById(userId)

        if (user === null) {
            throw UserNotFoundException(userId)
        }

        return user
    }

    suspend fun getUserByUsername(username: String): UserDto {
        val user = persistence.getUserByUsername(username)

        if (user === null) {
            throw NoSuchElementException("User with username $username not found")
        }

        return user
    }

    suspend fun createUser(username: String, password: String): UserDto {
        val userExists = persistence.getUserByUsername(username) != null

        if (userExists) {
            throw UserAlreadyExistsException(username)
        }

        return persistence.createUser(username, password)
    }

    suspend fun deleteUserById(id: UUID) = persistence.deleteUserById(id)
}


