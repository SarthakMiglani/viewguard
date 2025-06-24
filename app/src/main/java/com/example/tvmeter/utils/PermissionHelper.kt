package com.example.tvmeter.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.tvmeter.usage.UsageStatsCollector

class PermissionHelper(private val activity: ComponentActivity) {

    private lateinit var usageStatsLauncher: ActivityResultLauncher<Intent>
    private var onPermissionResult: ((Boolean) -> Unit)? = null

    fun init() {
        usageStatsLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val hasPermission = UsageStatsCollector.hasUsageStatsPermission(activity)
            onPermissionResult?.invoke(hasPermission)
        }
    }

    fun requestUsageStatsPermission(onResult: (Boolean) -> Unit) {
        if (UsageStatsCollector.hasUsageStatsPermission(activity)) {
            onResult(true)
            return
        }

        onPermissionResult = onResult

        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            // On some devices, we can directly open our app's usage access settings
            // but this might not work on all devices/TV platforms
        }

        try {
            usageStatsLauncher.launch(intent)
        } catch (e: Exception) {
            // If the specific settings page doesn't exist, open general settings
            val generalIntent = Intent(Settings.ACTION_SETTINGS)
            usageStatsLauncher.launch(generalIntent)
        }
    }

    companion object {
        fun hasUsageStatsPermission(context: Context): Boolean {
            return UsageStatsCollector.hasUsageStatsPermission(context)
        }

        fun showPermissionExplanation(): String {
            return """
                To track your TV usage, this app needs access to usage statistics.
                
                Please follow these steps:
                1. Go to Settings > Apps > Special app access
                2. Find "Usage access" or "Usage data access"
                3. Enable permission for TV Guardian
                
                This permission allows the app to see which apps you use and for how long, 
                helping you monitor your screen time effectively.
            """.trimIndent()
        }
    }
}