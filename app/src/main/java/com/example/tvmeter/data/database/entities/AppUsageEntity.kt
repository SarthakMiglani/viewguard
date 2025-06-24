package com.example.tvmeter.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage", primaryKeys = ["packageName", "date"])
data class AppUsageEntity(
    val packageName: String,
    val appName: String,
    val usageMinutes: Int,
    val weeklyUsageMinutes: Int,
    val monthlyUsageMinutes: Int,
    val dailyLimit: Int,
    val categoryId: Int,
    val lastUsed: Long,
    val iconResourceName: String? = null,
    val totalLaunchCount: Int = 0,
    val date: String,
    @ColumnInfo(name = "uploaded", defaultValue = "0")
    val uploaded: Boolean = false,
    @ColumnInfo(name = "last_sync", defaultValue = "0")
    val lastSync: Long = 0L
)
