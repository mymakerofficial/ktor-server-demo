package de.maiker.service

import de.maiker.models.UserDto
import de.maiker.persistence.UserPersistence
import java.util.*

class UserService{
    private val userPersistence = UserPersistence()
    private val fileService = FileService()

    suspend fun getAllUsers(): Result<List<UserDto>> =
        Result.runCatching { (userPersistence.getAllUsers()) }

    suspend fun getUserById(id: UUID): Result<UserDto> {
        val user = userPersistence.getUserById(id)

        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    suspend fun createUser(username: String, password: String): Result<UserDto> {
        val userExists = userPersistence.getUserByUsername(username) != null

        if (userExists) {
            return Result.failure(Exception("User already exists"))
        }

        return Result.runCatching {
            userPersistence.createUser(username, password)
        }
    }

    suspend fun getUserWithMatchingPassword(username: String, password: String): Result<UserDto> {
        val user = userPersistence.getUserByUsername(username)

        if (password != user?.password) {
            return Result.failure(Exception("Username or Password is incorrect"))
        }

        return Result.success(user)
    }

    suspend fun deleteUserById(id: UUID): Result<Unit> {
        getUserById(id).onFailure { return Result.failure(it) }

        val files = fileService.getAllFilesByUserId(id).getOrElse { return Result.failure(it) }

        files.forEach { file ->
            fileService.deleteFileById(file.id).onFailure { return Result.failure(it) }
        }

        runCatching {
            userPersistence.deleteUserById(id)
        }.onFailure {
            return Result.failure(it)
        }

        return Result.success(Unit)
    }
}


