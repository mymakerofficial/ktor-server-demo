package de.maiker.models

import io.github.smiley4.ktorswaggerui.dsl.Example
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
    @Example("1531068e-6210-46ac-ac74-38fbc1ed0fc7")
    val id: String,
    @Example("user")
    val username: String,
)

@Serializable
data class UserLoginResponse(
    @Example("1531068e-6210-46ac-ac74-38fbc1ed0fc7")
    val id: String,
    @Example("user")
    val username: String,
    @Example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    val token: String,
)

@Serializable
data class UserAuthRequest(
    @Example("user")
    val username: String,
    @Example("MySup3rS3cr3tP4ssw0rd")
    val password: String,
)