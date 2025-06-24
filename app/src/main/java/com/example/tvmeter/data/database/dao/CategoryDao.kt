package com.example.tvmeter.data.database.dao

import androidx.room.*
import com.example.tvmeter.data.database.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?
}