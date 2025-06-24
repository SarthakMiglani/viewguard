package com.example.tvmeter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmeter.data.database.dao.AppUsageDao
import com.example.tvmeter.data.database.dao.CategoryDao
import com.example.tvmeter.data.database.entities.AppUsageEntity
import com.example.tvmeter.usage.UsageStatsCollector
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: String,
    val usageMinutes: Int,
    val dailyLimit: Int,
    val categoryId: Int,
    val weeklyUsageMinutes: Int = 0,
    val lastUsed: Long = 0
)

class AppUsageViewModel(
    private val appUsageDao: AppUsageDao,
    private val categoryDao: CategoryDao,
    private val usageStatsCollector: UsageStatsCollector
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())

    // Real-time app usage data from database
    val appUsageList: StateFlow<List<AppInfo>> = appUsageDao.getTodayAppUsage(today)
        .map { entities ->
            entities.map { entity -> entity.toAppInfo() }
        }
        .catch {
            // If there's an error or no data, show dummy data as fallback
            emit(getDummyApps())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Top weekly apps for dashboard
    val topWeeklyApps: StateFlow<List<AppInfo>> = appUsageDao.getTopWeeklyApps()
        .map { entities ->
            entities.map { entity -> entity.toAppInfo() }
        }
        .catch {
            emit(getDummyApps().take(3)) // Show top 3 dummy apps as fallback
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedApp = MutableStateFlow<AppInfo?>(null)
    val selectedApp: StateFlow<AppInfo?> = _selectedApp.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _totalTodayUsage = MutableStateFlow(0)
    val totalTodayUsage: StateFlow<Int> = _totalTodayUsage.asStateFlow()

    private val _totalWeeklyUsage = MutableStateFlow(0)
    val totalWeeklyUsage: StateFlow<Int> = _totalWeeklyUsage.asStateFlow()

    init {
        // Load total usage stats
        loadTotalUsageStats()
    }

    fun selectApp(app: AppInfo) {
        _selectedApp.value = app
    }

    fun refreshUsageData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                usageStatsCollector.collectAndStoreUsageStats()
                loadTotalUsageStats()
            } catch (e: Exception) {
                // Handle error - could emit error state
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAppLimit(packageName: String, newLimit: Int) {
        viewModelScope.launch {
            try {
                val existingApp = appUsageDao.getAppUsageForDate(packageName, today)
                existingApp?.let {
                    val updatedApp = it.copy(dailyLimit = newLimit)
                    appUsageDao.updateAppUsage(updatedApp)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadTotalUsageStats() {
        viewModelScope.launch {
            try {
                val todayTotal = appUsageDao.getTotalUsageForDate(today) ?: 0
                val weeklyTotal = appUsageDao.getTotalWeeklyUsage() ?: 0

                _totalTodayUsage.value = todayTotal
                _totalWeeklyUsage.value = weeklyTotal
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Extension function to convert entity to AppInfo
    private fun AppUsageEntity.toAppInfo(): AppInfo {
        return AppInfo(
            packageName = this.packageName,
            name = this.appName,
            icon = getIconForApp(this.packageName), // We'll create this function
            usageMinutes = this.usageMinutes,
            dailyLimit = this.dailyLimit,
            categoryId = this.categoryId,
            weeklyUsageMinutes = this.weeklyUsageMinutes,
            lastUsed = this.lastUsed
        )
    }

    // Helper function to get emoji icon for apps
    private fun getIconForApp(packageName: String): String {
        return when {
            packageName.contains("netflix", ignoreCase = true) -> "🎬"
            packageName.contains("youtube", ignoreCase = true) -> "📺"
            packageName.contains("prime", ignoreCase = true) ||
                    packageName.contains("amazon", ignoreCase = true) -> "🎭"
            packageName.contains("disney", ignoreCase = true) -> "🏰"
            packageName.contains("spotify", ignoreCase = true) -> "🎵"
            packageName.contains("hulu", ignoreCase = true) -> "📱"
            packageName.contains("hbo", ignoreCase = true) -> "🎪"
            packageName.contains("twitch", ignoreCase = true) -> "🎮"
            packageName.contains("game", ignoreCase = true) -> "🎮"
            packageName.contains("music", ignoreCase = true) -> "🎶"
            packageName.contains("news", ignoreCase = true) -> "📰"
            packageName.contains("weather", ignoreCase = true) -> "🌤️"
            else -> "📱" // Default app icon
        }
    }

    // Fallback dummy data (same as before but with additional fields)
    private fun getDummyApps(): List<AppInfo> {
        return listOf(
            AppInfo("com.netflix.mediaclient", "Netflix", "🎬", 135, 180, 1, 850, System.currentTimeMillis()),
            AppInfo("com.google.android.youtube.tv", "YouTube", "📺", 65, 120, 1, 420, System.currentTimeMillis() - 3600000),
            AppInfo("com.amazon.avod.thirdpartyclient", "Prime Video", "🎭", 45, 90, 1, 280, System.currentTimeMillis() - 7200000),
            AppInfo("com.spotify.tv.android", "Spotify", "🎵", 25, 60, 2, 180, System.currentTimeMillis() - 10800000),
            AppInfo("com.disney.disneyplus", "Disney+", "🏰", 30, 60, 1, 200, System.currentTimeMillis() - 14400000)
        )
    }

    // Helper function to format usage time
    fun formatUsageTime(minutes: Int): String {
        return when {
            minutes >= 60 -> {
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours}h"
            }
            else -> "${minutes}m"
        }
    }

    // Helper function to check if app is over limit
    fun isOverLimit(app: AppInfo): Boolean {
        return app.usageMinutes > app.dailyLimit
    }

    // Get apps that are over their daily limit
    fun getOverLimitApps(): StateFlow<List<AppInfo>> {
        return appUsageList.map { apps ->
            apps.filter { isOverLimit(it) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}