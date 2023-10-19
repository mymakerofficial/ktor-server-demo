package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.mapper.toDto
import de.maiker.models.UserDao
import de.maiker.models.Users
import de.maiker.models.UserDto
import org.jetbrains.exposed.sql.*
import java.util.*

class UserPersistence {
    suspend fun getAllUsers(): List<UserDto> = dbQuery {
        UserDao.all().map { it.toDto() }
    }

    suspend fun getUserById(id: UUID): UserDto? = dbQuery {
        UserDao.findById(id)?.toDto()
    }

    suspend fun getUserByUsername(username: String): UserDto? = dbQuery {
        UserDao.find {
            Users.username eq username
        }.firstOrNull()?.toDto()
    }

    suspend fun createUser(newUsername: String, newPassword: String): UserDto = dbQuery {
        UserDao.new {
            username = newUsername
            password = newPassword
        }.toDto()
    }

    suspend fun deleteUserById(id: UUID) = dbQuery {
        Users.deleteWhere {
            Users.id eq id
        }
    }
}