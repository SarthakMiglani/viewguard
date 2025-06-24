package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PairDeviceRequest(
    val deviceId: String,
    val pairingCode: String
) 