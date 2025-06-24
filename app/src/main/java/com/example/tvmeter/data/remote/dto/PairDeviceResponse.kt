package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PairDeviceResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long // Seconds
) 