package com.example.tvmeter.utils

import android.content.Context
import androidx.work.WorkManager
import com.example.tvmeter.TvMeterApplication
import com.example.tvmeter.data.token.TokenManager
import com.example.tvmeter.workers.WorkManagerSetup

// Extension to get TokenManager from Context
fun Context.getTokenManager(): TokenManager {
    return (applicationContext as TvMeterApplication).appContainer.tokenManager
}

// Extension to check if device is paired
suspend fun Context.isDevicePaired(): Boolean {
    val tokenManager = getTokenManager()
    return tokenManager.getDeviceId() != null &&
            tokenManager.getAccessToken() != null &&
            tokenManager.isTokenValid()
}

// Extension to start monitoring after pairing
fun Context.startMonitoring() {
    WorkManagerSetup.setupPeriodicWork(this)
}

// Extension to stop monitoring
fun Context.stopMonitoring() {
    WorkManagerSetup.cancelAllWork(this)
} 