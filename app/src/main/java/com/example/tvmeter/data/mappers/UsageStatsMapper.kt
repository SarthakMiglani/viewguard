package com.example.tvmeter.data.mappers

import com.example.tvmeter.data.database.entities.AppUsageEntity
import com.example.tvmeter.data.remote.dto.UsageStatItem

object UsageStatsMapper {

    fun mapToNetworkDto(entity: AppUsageEntity): UsageStatItem {
        return UsageStatItem(
            packageName = entity.packageName,
            appName = entity.appName,
            usageMinutes = entity.usageMinutes.toLong(),
            weeklyUsageMinutes = entity.weeklyUsageMinutes.toLong(),
            monthlyUsageMinutes = entity.monthlyUsageMinutes.toLong(),
            dailyLimit = if (entity.dailyLimit > 0) entity.dailyLimit.toLong() else null,
            categoryId = if (entity.categoryId > 0) entity.categoryId.toLong() else null,
            lastUsed = entity.lastUsed,
            totalLaunchCount = entity.totalLaunchCount,
            date = entity.date
        )
    }

    fun mapToNetworkDtoList(entities: List<AppUsageEntity>): List<UsageStatItem> {
        return entities.map { mapToNetworkDto(it) }
    }
}
