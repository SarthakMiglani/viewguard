package com.example.tvmeter.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import com.example.tvmeter.data.database.AppDatabase
import com.example.tvmeter.usage.UsageStatsCollector
import java.util.concurrent.TimeUnit

class UsageStatsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val usageStatsCollector = UsageStatsCollector(
                applicationContext,
                database.appUsageDao()
            )

            // Check if we have permission
            if (UsageStatsCollector.hasUsageStatsPermission(applicationContext)) {
                usageStatsCollector.collectAndStoreUsageStats()
                Result.success()
            } else {
                // If we don't have permission, we should retry later
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "usage_stats_work"

        fun scheduleWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<UsageStatsWorker>(
                15, TimeUnit.MINUTES // Collect usage stats every 15 minutes
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueue(workRequest)
        }

        fun cancelWork(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORK_NAME)
        }
    }
}