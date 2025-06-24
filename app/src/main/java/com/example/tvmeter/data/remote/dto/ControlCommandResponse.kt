package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ControlCommandResponse(
    val commands: List<DeviceCommand>
)

@Serializable
data class DeviceCommand(
    val id: String,
    val type: String, // "BLOCK_APP", "UNBLOCK_APP", "SET_TIME_LIMIT", etc.
    val targetPackage: String?,
    val parameters: Map<String, String>?
) 