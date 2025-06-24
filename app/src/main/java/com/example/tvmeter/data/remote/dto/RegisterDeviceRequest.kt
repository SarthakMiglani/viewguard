package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceRequest(
    val deviceName: String,
    val deviceModel: String,
    val osVersion: String
) 