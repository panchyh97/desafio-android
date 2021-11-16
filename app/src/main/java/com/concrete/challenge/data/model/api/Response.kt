package com.concrete.challenge.data.model.api

sealed class Response<T> {
    data class OnSuccess<T>(val data: T?) : Response<T>()
    data class OnFailure<T>(val throwable: Throwable) : Response<T>()
    class OnLoading<T> : Response<T>()
}
