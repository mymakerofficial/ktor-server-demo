package de.maiker.persistence

import de.maiker.database.DatabaseFactory.dbQuery
import de.maiker.mapper.toDto
import de.maiker.models.UserDao
import de.maiker.models.UserDto
import de.maiker.models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class UserPersistence {
    suspend fun getAllUsers(): List<UserDto> = dbQuery {
        UserDao.all().map { it.toDto() }
    }

    suspend fun getUserById(id: UUID): UserDto? = dbQuery {
        UserDao.findById(id)?.toDto()
    }

    suspend fun getUserByUsername(username: String): UserDto? = dbQuery {
        UserDao.find { Users.username eq username }.firstOrNull()?.toDto()
    }

    suspend fun createUser(username: String, password: String): UserDto = dbQuery {
        UserDao.new {
            this.username = username
            this.password = password
        }.toDto()
    }

    suspend fun deleteUserById(id: UUID) = dbQuery {
        UserDao.findById(id)?.delete()
    }
}
