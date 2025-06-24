package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsageStatsResponse(
    val success: Boolean,
    val message: String
) 