package com.example.tvmeter.data.token

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "tv_meter_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) = withContext(Dispatchers.IO) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRY, expiryTime)
            apply()
        }
    }

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    suspend fun isTokenValid(): Boolean = withContext(Dispatchers.IO) {
        val expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        val currentTime = System.currentTimeMillis()
        val bufferTime = 5 * 60 * 1000 // 5 minutes buffer
        expiryTime > (currentTime + bufferTime)
    }

    suspend fun saveDeviceId(deviceId: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(KEY_DEVICE_ID, deviceId)
            .apply()
    }

    suspend fun getDeviceId(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_DEVICE_ID, null)
    }

    suspend fun clearAllTokens() = withContext(Dispatchers.IO) {
        with(sharedPreferences.edit()) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_DEVICE_ID)
            remove(KEY_TOKEN_EXPIRY)
            apply()
        }
    }

    suspend fun getBearerToken(): String? = withContext(Dispatchers.IO) {
        getAccessToken()?.let { "Bearer $it" }
    }
} 