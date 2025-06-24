package com.example.tvmeter.data.database.dao

import androidx.room.*
import com.example.tvmeter.data.database.entities.AppUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {

    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY usageMinutes DESC")
    fun getTodayAppUsage(date: String): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage ORDER BY usageMinutes DESC")
    fun getAllAppUsage(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage ORDER BY weeklyUsageMinutes DESC LIMIT 10")
    fun getTopWeeklyApps(): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName AND date = :date")
    suspend fun getAppUsageForDate(packageName: String, date: String): AppUsageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsage(appUsage: AppUsageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUsageList(appUsageList: List<AppUsageEntity>)

    @Update
    suspend fun updateAppUsage(appUsage: AppUsageEntity)

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName ORDER BY date DESC LIMIT 1")
    suspend fun getLatestAppUsage(packageName: String): AppUsageEntity?

    @Query("DELETE FROM app_usage WHERE date < :cutoffDate")
    suspend fun deleteOldUsageData(cutoffDate: String)

    @Query("SELECT SUM(usageMinutes) FROM app_usage WHERE date = :date")
    suspend fun getTotalUsageForDate(date: String): Int?

    @Query("SELECT SUM(weeklyUsageMinutes) FROM app_usage")
    suspend fun getTotalWeeklyUsage(): Int?

    @Query("SELECT * FROM app_usage WHERE date = :date")
    suspend fun getUsageStatsForDate(date: String): List<AppUsageEntity>

    @Query("SELECT * FROM app_usage WHERE date >= :startDate AND date <= :endDate")
    suspend fun getUsageStatsForDateRange(startDate: String, endDate: String): List<AppUsageEntity>

    @Query("UPDATE app_usage SET uploaded = 1 WHERE packageName IN (:packageNames) AND date = :date")
    suspend fun markStatsAsUploaded(packageNames: List<String>, date: String)

    @Query("SELECT * FROM app_usage WHERE uploaded = 0 AND date = :date")
    suspend fun getUnuploadedStatsForDate(date: String): List<AppUsageEntity>
}
