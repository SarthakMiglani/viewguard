package com.example.tvmeter.usage

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.example.tvmeter.data.database.dao.AppUsageDao
import com.example.tvmeter.data.database.entities.AppUsageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UsageStatsCollector(
    private val context: Context,
    private val appUsageDao: AppUsageDao
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun collectAndStoreUsageStats() {
        withContext(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis

                // Get today's usage (from midnight to now)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startTime = calendar.timeInMillis

                // Get weekly usage (last 7 days)
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val weekStartTime = calendar.timeInMillis

                // Get usage stats for today
                val todayStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )

                // Get usage stats for the week
                val weeklyStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_WEEKLY,
                    weekStartTime,
                    endTime
                )

                val today = dateFormat.format(Date(startTime))
                val appUsageList = mutableListOf<AppUsageEntity>()

                // Process today's stats
                todayStats?.forEach { usageStats ->
                    if (usageStats.totalTimeInForeground > 0) {
                        val packageName = usageStats.packageName
                        val appInfo = getAppInfo(packageName)

                        if (appInfo != null && isRelevantApp(packageName, appInfo)) {
                            val todayMinutes = (usageStats.totalTimeInForeground / (1000 * 60)).toInt()
                            val weeklyMinutes = getWeeklyUsage(packageName, weeklyStats)

                            val existingUsage = appUsageDao.getAppUsageForDate(packageName, today)
                            val dailyLimit = existingUsage?.dailyLimit ?: getDefaultDailyLimit(packageName)
                            val categoryId = existingUsage?.categoryId ?: getCategoryForApp(packageName)

                            val appUsageEntity = AppUsageEntity(
                                packageName = packageName,
                                appName = appInfo.loadLabel(packageManager).toString(),
                                usageMinutes = todayMinutes,
                                weeklyUsageMinutes = weeklyMinutes,
                                monthlyUsageMinutes = 0, // We'll implement monthly tracking later
                                dailyLimit = dailyLimit,
                                categoryId = categoryId,
                                lastUsed = usageStats.lastTimeUsed,
                                totalLaunchCount = 0, // We'll get this from launch count if needed
                                date = today
                            )

                            appUsageList.add(appUsageEntity)
                        }
                    }
                }

                // Store in database
                @Suppress("UNUSED_VARIABLE")
                val nothing = if (appUsageList.isNotEmpty()) {
                    appUsageDao.insertAppUsageList(appUsageList)
                    Log.d("UsageStatsCollector", "Stored ${appUsageList.size} app usage records")
                    true
                } else {
                    false
                }



            } catch (e: Exception) {
                Log.e("UsageStatsCollector", "Error collecting usage stats", e)
            }
        }
    }

    private fun getWeeklyUsage(packageName: String, weeklyStats: List<UsageStats>?): Int {
        return weeklyStats?.find { it.packageName == packageName }
            ?.let { (it.totalTimeInForeground / (1000 * 60)).toInt() } ?: 0
    }

    private fun getAppInfo(packageName: String): ApplicationInfo? {
        return try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun isRelevantApp(packageName: String, appInfo: ApplicationInfo): Boolean {
        // Filter out system apps and irrelevant packages
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isLaunchable = packageManager.getLaunchIntentForPackage(packageName) != null

        // Include entertainment apps even if they're system apps (like some TV apps)
        val entertainmentPackages = listOf(
            "netflix", "youtube", "prime", "disney", "hulu", "hbo"
        )
        val isEntertainmentApp = entertainmentPackages.any { packageName.contains(it, ignoreCase = true) }

        return isLaunchable || isEntertainmentApp || !isSystemApp
    }

    private fun getDefaultDailyLimit(packageName: String): Int {
        // Set default limits based on app type
        return when {
            packageName.contains("netflix") || packageName.contains("youtube") -> 180
            packageName.contains("game") -> 120
            else -> 120
        }
    }

    private fun getCategoryForApp(packageName: String): Int {
        // Simple categorization logic
        return when {
            packageName.contains("netflix") || packageName.contains("youtube") ||
                    packageName.contains("prime") || packageName.contains("disney") -> 1 // Entertainment
            packageName.contains("game") -> 2 // Games
            else -> 3 // Other
        }
    }

    companion object {
        fun hasUsageStatsPermission(context: Context): Boolean {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val startTime = calendar.timeInMillis

            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            return usageStatsList != null && usageStatsList.isNotEmpty()
        }
    }
}