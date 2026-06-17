package com.devscion.chapterstage.core.common

sealed class DomainException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : DomainException("Network error", cause)
    class Server(val code: Int, message: String) : DomainException(message)
    class NotFound(resource: String) : DomainException("$resource not found")
    data object Unauthorized : DomainException("Unauthorized")
    class Validation(val code: String, message: String) : DomainException(message)
    class Unknown(cause: Throwable? = null) : DomainException("Unknown error", cause)
}
