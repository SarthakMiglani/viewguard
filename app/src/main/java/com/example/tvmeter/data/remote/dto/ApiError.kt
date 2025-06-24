package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: String? = null
) 