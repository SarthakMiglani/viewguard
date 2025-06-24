package com.example.tvmeter.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class UsageStatsRequest(
    val deviceId: String,
    @SerialName("appStats")
    val appstats: List<UsageStatItem>,
    val timestamp: Long,
    val reportDate: String
)


@Serializable
data class UsageStatItem(
    val packageName: String,
    val appName: String,
    val usageMinutes: Long, // Today's usage in minutes
    val weeklyUsageMinutes: Long,
    val monthlyUsageMinutes: Long,
    val dailyLimit: Long?, // In minutes, null if no limit
    val categoryId: Long?,
    val lastUsed: Long, // Unix timestamp in milliseconds
    val totalLaunchCount: Int,
    val date: String // YYYY-MM-DD format
) 