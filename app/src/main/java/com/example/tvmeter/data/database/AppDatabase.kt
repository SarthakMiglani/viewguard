package com.example.tvmeter.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.tvmeter.data.database.dao.AppUsageDao
import com.example.tvmeter.data.database.dao.CategoryDao
import com.example.tvmeter.data.database.entities.AppUsageEntity
import com.example.tvmeter.data.database.entities.CategoryEntity
@Database(
    entities = [AppUsageEntity::class, CategoryEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appUsageDao(): AppUsageDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tv_guardian_database"
                )
                    .fallbackToDestructiveMigration() // 💥 Auto-wipe DB when schema changes
                    .allowMainThreadQueries()         // Only for testing – avoid in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
