package com.example.tvmeter.data.remote

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.tvmeter.data.remote.dto.*
import com.example.tvmeter.data.token.TokenManager
import com.example.tvmeter.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class DeviceRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val context: Context
) {
    suspend fun registerDevice(): Flow<NetworkResult<RegisterDeviceResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val deviceName = Build.MODEL
            val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            val osVersion = "Android ${Build.VERSION.RELEASE}"
            val request = RegisterDeviceRequest(
                deviceName = deviceName,
                deviceModel = deviceModel,
                osVersion = osVersion
            )
            val response = apiService.registerDevice(request)
            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    tokenManager.saveDeviceId(registerResponse.deviceId)
                    emit(NetworkResult.Success(registerResponse))
                } ?: emit(NetworkResult.Error("Empty response body"))
            } else {
                emit(NetworkResult.Error("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }

    suspend fun pairDevice(pairingCode: String): Flow<NetworkResult<PairDeviceResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val deviceId = tokenManager.getDeviceId()
            if (deviceId == null) {
                emit(NetworkResult.Error("Device not registered"))
                return@flow
            }
            val request = PairDeviceRequest(
                deviceId = deviceId,
                pairingCode = pairingCode
            )
            val response = apiService.pairDevice(request)
            if (response.isSuccessful) {
                response.body()?.let { pairResponse ->
                    tokenManager.saveTokens(
                        accessToken = pairResponse.accessToken,
                        refreshToken = pairResponse.refreshToken,
                        expiresIn = pairResponse.expiresIn
                    )
                    emit(NetworkResult.Success(pairResponse))
                } ?: emit(NetworkResult.Error("Empty response body"))
            } else {
                emit(NetworkResult.Error("Pairing failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }

    suspend fun uploadUsageStats(
        stats: List<UsageStatItem>,
        reportDate: String
    ): Flow<NetworkResult<UsageStatsResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val deviceId = tokenManager.getDeviceId() ?: "TEST-DEVICE-ID"
            val token = tokenManager.getBearerToken() ?: "Bearer TEST-TOKEN"

            Log.d("API_UPLOAD", "Device ID: $deviceId")
            Log.d("API_UPLOAD", "Bearer token: $token")
            Log.d("API_UPLOAD", "Prepared request: stats=${stats.size}, reportDate=$reportDate")

            val request = UsageStatsRequest(
                deviceId = deviceId,
                appstats = stats,
                timestamp = System.currentTimeMillis(),
                reportDate = reportDate
            )

            Log.d("API_UPLOAD", "Sending request body: $request")

            val response = apiService.uploadUsageStats(token, request)

            Log.d("API_UPLOAD", "HTTP ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                response.body()?.let { uploadResponse ->
                    Log.d("API_UPLOAD", "Success response: $uploadResponse")
                    emit(NetworkResult.Success(uploadResponse))
                } ?: run {
                    Log.e("API_UPLOAD", "Empty response body")
                    emit(NetworkResult.Error("Empty response body"))
                }
            } else {
                Log.e("API_UPLOAD", "Upload failed with code ${response.code()} - ${response.message()}")
                emit(NetworkResult.Error("Upload failed: ${response.message()}"))
            }

        } catch (e: Exception) {
            Log.e("API_UPLOAD", "Exception during upload", e)
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }


    suspend fun getControlCommands(): Flow<NetworkResult<ControlCommandResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val deviceId = tokenManager.getDeviceId()
            val token = tokenManager.getBearerToken()
            if (deviceId == null || token == null) {
                emit(NetworkResult.Error("Device not paired"))
                return@flow
            }
            val response = apiService.getControlCommands(token, deviceId)
            if (response.isSuccessful) {
                response.body()?.let { commandResponse ->
                    emit(NetworkResult.Success(commandResponse))
                } ?: emit(NetworkResult.Error("Empty response body"))
            } else {
                emit(NetworkResult.Error("Failed to get commands: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Network error: ${e.message}"))
        }
    }

    suspend fun refreshTokenIfNeeded(): Boolean {
        try {
            if (tokenManager.isTokenValid()) {
                return true
            }
            val refreshToken = tokenManager.getRefreshToken() ?: return false
            val response = apiService.refreshToken("Bearer $refreshToken")
            if (response.isSuccessful) {
                response.body()?.let { tokenResponse ->
                    tokenManager.saveTokens(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken,
                        expiresIn = tokenResponse.expiresIn
                    )
                    return true
                }
            }
        } catch (e: Exception) {
            // Log error
        }
        return false
    }
} 