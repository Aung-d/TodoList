package com.code.winter.ako.todolist.data.db.dao

import androidx.room.*
import com.code.winter.ako.todolist.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(vararg category: Category)

    @Query("SELECT * FROM tbl_category")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT categoryName,(SELECT COUNT(*) FROM tbl_task WHERE tbl_task.categoryName = tbl_category.categoryName) AS taskCount FROM tbl_category WHERE editable = 1 ORDER BY createdDate DESC")
    fun getEditableCategoriesSortedByCreateDate(): Flow<List<ManageCategory>>

    @Query("SELECT categoryName,(SELECT COUNT(*) FROM tbl_task WHERE tbl_task.categoryName = tbl_category.categoryName) AS taskCount FROM tbl_category WHERE editable = 1 ORDER BY categoryName")
    fun getEditableCategoriesSortedByCategoryName(): Flow<List<ManageCategory>>

    @Query("DELETE FROM tbl_category WHERE categoryName = :categoryName")
    suspend fun deleteCategoryByCategoryName(categoryName: String)

    @Query("UPDATE tbl_category SET categoryName = :newCategoryName WHERE categoryName = :categoryName")
    suspend fun updateCategoryNameByCategoryName(newCategoryName: String, categoryName: String)
}