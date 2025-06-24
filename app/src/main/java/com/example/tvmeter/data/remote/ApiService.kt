package com.example.tvmeter.data.remote

import com.example.tvmeter.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/device/register")
    suspend fun registerDevice(
        @Body request: RegisterDeviceRequest
    ): Response<RegisterDeviceResponse>

    @POST("api/device/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): Response<PairDeviceResponse>

    @POST("api/usage/stats")
    suspend fun uploadUsageStats(
        @Header("Authorization") token: String,
        @Body request: UsageStatsRequest
    ): Response<UsageStatsResponse>

    @GET("api/control/{deviceId}")
    suspend fun getControlCommands(
        @Header("Authorization") token: String,
        @Path("deviceId") deviceId: String
    ): Response<ControlCommandResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<PairDeviceResponse>
} 