package de.maiker.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

object Users: UUIDTable("users") {
    val username = varchar("username", 255)
    val password = varchar("password", 255)
}

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(Users)

    var username by Users.username
    var password by Users.password
}

data class UserDto(
    val id: UUID? = null,
    val username: String,
    val password: String,
)

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
)

@Serializable
data class UserLoginResponse(
    val id: String,
    val username: String,
    val token: String,
)

@Serializable
data class UserAuthRequest(
    val username: String,
    val password: String,
)