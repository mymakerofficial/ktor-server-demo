package de.maiker.exceptions

import io.ktor.http.*
import java.util.*

open class UserNotFoundException(id: UUID) : NoSuchElementException("User with id $id not found")
open class MediaNotFoundException(id: UUID) : NoSuchElementException("Media with id $id not found")
open class MediaFileNotFoundException(id: UUID) : NoSuchElementException("MediaFile with id $id not found")

open class UserAlreadyExistsException(username: String) : Exception("User with username $username already exists")

open class AccessDeniedException(message: String?) : Exception(message ?: "Access denied")
open class UsernameOrPasswordIncorrectException : AccessDeniedException("Username or Password is incorrect")

open class UnsupportedContentTypeException(contentType: ContentType) : IllegalArgumentException("Content-Type $contentType is not supported")

open class FailedToCreateException(message: String?) : Exception(message ?: "Failed to create resource")
open class FailedToReadException(message: String?) : Exception(message ?: "Failed to read resource")
open class FailedToUpdateException(message: String?) : Exception(message ?: "Failed to update resource")
open class FailedToDeleteException(message: String?) : Exception(message ?: "Failed to delete resource")