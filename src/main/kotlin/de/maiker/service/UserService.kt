package de.maiker.service

import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserService{
    private val userPersistence = UserPersistence()

    suspend fun getAllUsers(): List<UserDto> =
        userPersistence.getAllUsers()

    suspend fun getUserById(id: UUID): UserDto? =
        userPersistence.getUserById(id)

    suspend fun createUser(username: String, password: String): UserDto? {
        val userExists = userPersistence.getUserByUsername(username) != null

        if (userExists) {
            return null
        }

        return userPersistence.createUser(username, password)
    }

    suspend fun loginAndGetUser(username: String, password: String): UserDto? {
        val user = userPersistence.getUserByUsername(username)

        if (user?.password == password) {
            return user
        }

        return null
    }
}

