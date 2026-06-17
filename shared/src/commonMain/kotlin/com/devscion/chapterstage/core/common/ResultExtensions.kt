package com.devscion.chapterstage.core.common

inline fun <T> Result<T>.mapError(transform: (Throwable) -> DomainException): Result<T> =
    recoverCatching { throw transform(it) }
