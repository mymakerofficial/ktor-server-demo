package de.maiker.service

import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserService{
    private val userPersistence = UserPersistence()

    suspend fun getAllUsers(): Result<List<UserDto>> =
        Result.success(userPersistence.getAllUsers())

    suspend fun getUserById(id: UUID): Result<UserDto> {
        val user = userPersistence.getUserById(id)

        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(error("User not found"))
        }
    }

    suspend fun createUser(username: String, password: String): Result<UserDto> {
        val userExists = userPersistence.getUserByUsername(username) != null

        if (userExists) {
            return Result.failure(error("User already exists"))
        }

        val user = userPersistence.createUser(username, password)

        return Result.success(user)
    }

    suspend fun getUserWithMatchingPassword(username: String, password: String): Result<UserDto> {
        val user = userPersistence.getUserByUsername(username)

        if (password != user?.password) {
            return Result.failure(error("Username or Password is incorrect"))
        }

        return Result.success(user)
    }

    suspend fun deleteUserById(id: UUID) =
        userPersistence.deleteUserById(id)
}


