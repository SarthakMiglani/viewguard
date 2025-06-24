package com.example.tvmeter.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.tvmeter.TvMeterApplication
import com.example.tvmeter.data.database.AppDatabase
import com.example.tvmeter.data.database.entities.AppUsageEntity
import com.example.tvmeter.data.mappers.UsageStatsMapper
import com.example.tvmeter.data.remote.DeviceRepository
import com.example.tvmeter.utils.NetworkConnectivity
import com.example.tvmeter.utils.NetworkResult
import kotlinx.coroutines.flow.last
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UsageStatsUploadWorker(
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

            // Get Room database
            val database = AppDatabase.getDatabase(applicationContext)
            val appUsageDao = database.appUsageDao()

            // Refresh token if needed
            if (!repository.refreshTokenIfNeeded()) {
                return Result.failure(
                    Data.Builder()
                        .putString("error", "Authentication failed")
                        .build()
                )
            }

            // Get today's date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())


            //test start

            val testApps = listOf(
                AppUsageEntity(
                    packageName = "com.example.testapp1",
                    appName = "Test App 1",
                    usageMinutes = 10,
                    weeklyUsageMinutes = 50,
                    monthlyUsageMinutes = 100,
                    dailyLimit = 60,
                    categoryId = 1,
                    lastUsed = System.currentTimeMillis(),
                    date = today
                ),
                AppUsageEntity(
                    packageName = "com.example.testapp2",
                    appName = "Test App 2",
                    usageMinutes = 20,
                    weeklyUsageMinutes = 70,
                    monthlyUsageMinutes = 150,
                    dailyLimit = 90,
                    categoryId = 2,
                    lastUsed = System.currentTimeMillis(),
                    date = today
                )
            )

// Insert sample data for test
            appUsageDao.insertAppUsageList(testApps)

            //test end





            // Get usage stats from Room database for today
            val usageEntities = appUsageDao.getUsageStatsForDate(today)

            if (usageEntities.isEmpty()) {
                return Result.success(
                    Data.Builder()
                        .putString("message", "No usage stats to upload")
                        .build()
                )
            }

            // Convert Room entities to network DTOs
            val statItems = UsageStatsMapper.mapToNetworkDtoList(usageEntities)
            Log.d("API_UPLOAD", "Prepared statItems: $statItems")
            Log.d("API_UPLOAD", "Prepared ${statItems.size} app(s) for upload")
            Log.d("API_UPLOAD", "Final request JSON: ${statItems.map { it.toString() }}")


            // Upload to server
            val result = repository.uploadUsageStats(statItems, today).last()

            when (result) {
                is NetworkResult.Success -> {
                    // Mark entities as uploaded (you might want to add an 'uploaded' flag to AppUsageEntity)
                    Result.success(
                        Data.Builder()
                            .putInt("uploaded_count", statItems.size)
                            .putString("date", today)
                            .build()
                    )
                }
                is NetworkResult.Error -> {
                    if (runAttemptCount < 3) {
                        Result.retry()
                    } else {
                        Result.failure(
                            Data.Builder()
                                .putString("error", result.message)
                                .build()
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure(
                    Data.Builder()
                        .putString("error", e.message)
                        .build()
                )
            }
        }
    }

    companion object {
        const val WORK_NAME = "usage_stats_upload"

        fun createOneTimeWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<UsageStatsUploadWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )

                .build()
        }

        fun createPeriodicWork(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<UsageStatsUploadWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
        }
    }
}
