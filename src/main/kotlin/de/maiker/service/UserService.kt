package de.maiker.service

import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserService{
    private val userPersistence = UserPersistence()
    private val fileService = FileService()

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

    suspend fun deleteUserById(id: UUID): Result<Unit> = Result.runCatching {
        getUserById(id).onFailure { throw it }

        val files = fileService.getAllFilesByUserId(id).getOrElse { throw it }

        files.forEach { file ->
            fileService.deleteFileById(file.id).onFailure { throw it }
        }

        userPersistence.deleteUserById(id)
    }
}


