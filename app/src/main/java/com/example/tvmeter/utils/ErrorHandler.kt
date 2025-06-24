package com.example.tvmeter.utils

import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {
    fun handleApiError(response: Response<*>): String {
        return when (response.code()) {
            400 -> "Bad request - Please check your input"
            401 -> "Unauthorized - Please pair your device again"
            403 -> "Forbidden - Access denied"
            404 -> "Not found - Service unavailable"
            408 -> "Request timeout - Please try again"
            429 -> "Too many requests - Please wait and try again"
            500 -> "Server error - Please try again later"
            502, 503 -> "Service unavailable - Please try again later"
            else -> "Unknown error occurred (${response.code()})"
        }
    }

    fun handleNetworkError(exception: Throwable): String {
        return when (exception) {
            is UnknownHostException -> "No internet connection"
            is SocketTimeoutException -> "Connection timeout - Please try again"
            is IOException -> "Network error - Please check your connection"
            else -> "Unexpected error: ${exception.message}"
        }
    }
} 