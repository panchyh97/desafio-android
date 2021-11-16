package com.concrete.challenge.utils

import retrofit2.HttpException
import java.net.HttpURLConnection

fun Throwable.isNotFound() = when (this) {
    is HttpException -> (code() == HttpURLConnection.HTTP_NOT_FOUND)
    else -> false
}