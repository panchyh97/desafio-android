package com.concrete.challenge.utils

import retrofit2.HttpException
import java.net.HttpURLConnection

fun Throwable.isNotFound() = when (this) {
    is HttpException -> (code() == HttpURLConnection.HTTP_NOT_FOUND)
    else -> false
}

fun Throwable.isInternalServerError() = when (this) {
    is HttpException -> (code() == HttpURLConnection.HTTP_INTERNAL_ERROR)
    else -> false
}

fun Throwable.isTimeout() = when (this) {
    is HttpException -> (code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
    else -> false
}