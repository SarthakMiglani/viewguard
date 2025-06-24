package com.example.tvmeter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmeter.data.database.dao.CategoryDao
import com.example.tvmeter.data.database.entities.CategoryEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Category(
    val id: Int,
    val name: String,
    val appCount: Int
)

class CategoriesViewModel(
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val _categories = MutableStateFlow(getDummyCategories())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    fun addCategory() {
        val newCategory = Category(
            id = _categories.value.size + 1,
            name = "New Category ${_categories.value.size + 1}",
            appCount = 0
        )
        _categories.value = _categories.value + newCategory

        viewModelScope.launch {
            categoryDao.insertCategory(
                CategoryEntity(name = newCategory.name)
            )
        }
    }

    private fun getDummyCategories(): List<Category> {
        return listOf(
            Category(1, "Entertainment", 5),
            Category(2, "Music", 2),
            Category(3, "Games", 3),
            Category(4, "Kids", 2)
        )
    }
}