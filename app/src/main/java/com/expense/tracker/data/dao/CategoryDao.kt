package com.expense.tracker.data.dao

import androidx.room.*
import com.expense.tracker.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isActive = 1 AND parentCategoryId IS NULL ORDER BY name")
    fun getMainCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE isActive = 1 AND parentCategoryId = :parentId ORDER BY name")
    fun getSubCategories(parentId: Long): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?

    @Query("SELECT * FROM categories WHERE name = :name AND parentCategoryId IS NULL LIMIT 1")
    suspend fun getMainCategoryByName(name: String): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}
