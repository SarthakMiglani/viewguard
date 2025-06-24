package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceResponse(
    val deviceId: String,
    val pairingCode: String,
    val expiresAt: Long // Unix timestamp
) 