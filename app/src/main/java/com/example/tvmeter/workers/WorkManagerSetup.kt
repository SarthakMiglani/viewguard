package com.example.tvmeter.workers

import android.content.Context
import android.util.Log
import androidx.work.*

object WorkManagerSetup {
    fun setupPeriodicWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        // Schedule usage stats upload
        val usageStatsWork = UsageStatsUploadWorker.createPeriodicWork()
        workManager.enqueueUniquePeriodicWork(
            UsageStatsUploadWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            usageStatsWork
        )
        // Schedule control commands polling
        val controlCommandsWork = ControlCommandsWorker.createPeriodicWork()
        workManager.enqueueUniquePeriodicWork(
            ControlCommandsWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            controlCommandsWork
        )
        Log.d("WORK_SETUP", "Enqueuing periodic upload work")

    }

    fun scheduleImmediateUpload(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val immediateWork = UsageStatsUploadWorker.createOneTimeWork()
        workManager.enqueueUniqueWork(
            "immediate_upload",
            ExistingWorkPolicy.REPLACE,
            immediateWork
        )
    }

    fun cancelAllWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(UsageStatsUploadWorker.WORK_NAME)
        workManager.cancelUniqueWork(ControlCommandsWorker.WORK_NAME)
    }
} 