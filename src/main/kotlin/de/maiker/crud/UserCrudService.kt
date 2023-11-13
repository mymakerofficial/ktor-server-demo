package de.maiker.crud

import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserCrudService{
    private val userPersistence = UserPersistence()

    suspend fun getAllUsers(): Result<List<UserDto>> = Result.runCatching {
        userPersistence.getAllUsers()
    }

    suspend fun getUserById(id: UUID): Result<UserDto> = Result.runCatching {
        val user = userPersistence.getUserById(id)

        if (user === null) {
            throw Exception("User not found")
        }

        user
    }

    suspend fun createUser(username: String, password: String): Result<UserDto> = Result.runCatching {
        val userExists = userPersistence.getUserByUsername(username) != null

        if (userExists) {
            throw Exception("User already exists")
        }

        userPersistence.createUser(username, password)
    }

    suspend fun getUserWithMatchingPassword(username: String, password: String): Result<UserDto> = Result.runCatching {
        val user = userPersistence.getUserByUsername(username)

        if (password != user?.password) {
            throw Exception("Username or Password is incorrect")
        }

        user
    }

    suspend fun deleteUserById(id: UUID) {
        userPersistence.deleteUserById(id)
    }
}


