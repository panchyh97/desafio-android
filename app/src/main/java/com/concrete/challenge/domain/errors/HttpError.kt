package com.concrete.challenge.domain.errors

sealed class HttpError{
    object NotFound : HttpError()
    object InternalServerError : HttpError()
    object Timeout : HttpError()
    object GenericError: HttpError()
}
