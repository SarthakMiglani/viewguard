package com.example.tvmeter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.tvmeter.data.database.AppDatabase
import com.example.tvmeter.ui.TVGuardianApp
import com.example.tvmeter.ui.activities.PairingActivity
import com.example.tvmeter.ui.theme.TVMeterTheme
import com.example.tvmeter.usage.UsageStatsCollector
import com.example.tvmeter.utils.PermissionHelper
import com.example.tvmeter.utils.startMonitoring
import com.example.tvmeter.workers.UsageStatsWorker
import com.example.tvmeter.workers.WorkManagerSetup
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var usageStatsCollector: UsageStatsCollector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = (application as TvMeterApplication).appContainer.tokenManager

        // ✅ Register result launcher before coroutine
        val permissionHelper = PermissionHelper(this)
        permissionHelper.init()

        // Proceed to coroutine
        lifecycleScope.launch {
            if (true) { // bypass pairing
                tokenManager.saveDeviceId("TEST-DEVICE-ID")
                tokenManager.saveTokens(
                    accessToken = "TEST-ACCESS-TOKEN",
                    refreshToken = "TEST-REFRESH-TOKEN",
                    expiresIn = 3600
                )

                setupUIAndTracking(permissionHelper) // Pass the helper
            } else {
                startActivity(Intent(this@MainActivity, PairingActivity::class.java))
                finish()
            }
        }
    }




    private fun setupUIAndTracking(permissionHelper: PermissionHelper) {
        val database = AppDatabase.getDatabase(this)
        usageStatsCollector = UsageStatsCollector(this, database.appUsageDao())
        this.permissionHelper = permissionHelper // Save the passed one

        val needsPermission = !UsageStatsCollector.hasUsageStatsPermission(this)

        setContent {
            var showPermissionDialog by remember { mutableStateOf(needsPermission) }
            var showDeniedDialog by remember { mutableStateOf(false) }

            TVMeterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TVGuardianApp()

                    if (showPermissionDialog) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = { Text("Usage Access Required") },
                            text = { Text(PermissionHelper.showPermissionExplanation()) },
                            confirmButton = {
                                TextButton(onClick = {
                                    this.permissionHelper.requestUsageStatsPermission { granted ->
                                        if (granted) {
                                            showPermissionDialog = false
                                            startUsageTracking()
                                        } else {
                                            showPermissionDialog = false
                                            showDeniedDialog = true
                                        }
                                    }
                                }) {
                                    Text("Grant Permission")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showPermissionDialog = false
                                }) {
                                    Text("Skip")
                                }
                            }
                        )
                    }

                    if (showDeniedDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeniedDialog = false },
                            title = { Text("Permission Required") },
                            text = {
                                Text("Without usage access permission, the app will show sample data only. You can grant permission later in Settings.")
                            },
                            confirmButton = {
                                TextButton(onClick = { showDeniedDialog = false }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {}
                        )
                    }
                }
            }

            if (!needsPermission) {
                startUsageTracking()
            }
        }
    }


    private fun startUsageTracking() {
        UsageStatsWorker.scheduleWork(this)
        lifecycleScope.launch {
            try {
                usageStatsCollector.collectAndStoreUsageStats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        startMonitoring()
        WorkManagerSetup.scheduleImmediateUpload(this)
    }
}
