package com.example.tvmeter.workers

import android.content.Context
import androidx.work.*
import com.example.tvmeter.TvMeterApplication
import com.example.tvmeter.data.remote.DeviceRepository
import com.example.tvmeter.utils.NetworkConnectivity
import com.example.tvmeter.utils.NetworkResult
import kotlinx.coroutines.flow.last
import java.util.concurrent.TimeUnit

class ControlCommandsWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            // Check network connectivity
            if (!NetworkConnectivity.isNetworkAvailable(applicationContext)) {
                return Result.retry()
            }
            // Get dependencies from Application
            val app = applicationContext as TvMeterApplication
            val repository = app.appContainer.deviceRepository
            // Refresh token if needed
            if (!repository.refreshTokenIfNeeded()) {
                return Result.failure()
            }
            // Get control commands
            val result = repository.getControlCommands().last()
            when (result) {
                is NetworkResult.Success -> {
                    result.data?.commands?.forEach { command ->
                        // Process each command
                        processCommand(command)
                    }
                    Result.success()
                }
                is NetworkResult.Error -> {
                    Result.retry()
                }
                is NetworkResult.Loading -> {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun processCommand(command: com.example.tvmeter.data.remote.dto.DeviceCommand) {
        // Process commands based on type
        when (command.type) {
            "BLOCK_APP", "UNBLOCK_APP" -> {
                // Implement app blocking/unblocking logic
                command.targetPackage?.let { packageName ->
                    // Your app control logic here
                }
            }
            "SET_TIME_LIMIT" -> {
                // Implement time limit setting
                command.parameters?.get("limit")?.let { limit ->
                    // Your time limit logic here
                }
            }
            // Add more command types as needed
        }
    }

    companion object {
        const val WORK_NAME = "control_commands_poll"
        fun createPeriodicWork(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<ControlCommandsWorker>(
                5, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }
} 