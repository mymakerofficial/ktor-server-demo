package de.maiker.mapper

import de.maiker.models.UserDao
import de.maiker.models.UserDto
import de.maiker.models.UserLoginResponse
import de.maiker.models.UserResponse

fun UserDao.toDto() = UserDto(
    id = this.id.value,
    username = this.username,
    password = this.password,
)

fun UserDto.toResponse() = UserResponse(
    id = this.id.toString(),
    username = this.username,
)

fun List<UserDto>.toResponse() = this.map { it.toResponse() }

fun UserResponse.withToken(token: String) = UserLoginResponse(
    id = this.id,
    username = this.username,
    token = token,
)