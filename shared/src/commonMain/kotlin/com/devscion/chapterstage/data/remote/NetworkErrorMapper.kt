package com.devscion.chapterstage.data.remote

import com.devscion.chapterstage.core.common.DomainException
import com.devscion.chapterstage.data.dto.ApiErrorResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

private val ErrorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

suspend fun <T> apiResult(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (throwable: Throwable) {
        Result.failure(throwable.toDomainException())
    }

suspend fun Throwable.toDomainException(): DomainException =
    when (this) {
        is DomainException -> this
        is ClientRequestException -> when (response.status) {
            HttpStatusCode.NotFound -> DomainException.NotFound("ChapterStage resource")
            HttpStatusCode.Unauthorized -> DomainException.Unauthorized
            HttpStatusCode.BadRequest,
            HttpStatusCode.UnprocessableEntity -> apiErrorBody()?.let { error ->
                DomainException.Validation(code = error.code, message = error.message)
            } ?: DomainException.Server(code = response.status.value, message = message)
            else -> apiErrorBody()?.let { error ->
                DomainException.Server(code = response.status.value, message = error.message)
            } ?: DomainException.Server(code = response.status.value, message = message)
        }
        is ServerResponseException -> apiErrorBody()?.let { error ->
            DomainException.Server(code = response.status.value, message = error.message)
        } ?: DomainException.Server(code = response.status.value, message = message)
        is IOException -> DomainException.Network(this)
        else -> DomainException.Unknown(this)
    }

private suspend fun ResponseException.apiErrorBody() =
    runCatching {
        ErrorJson.decodeFromString<ApiErrorResponse>(response.bodyAsText()).error
    }.getOrNull()
