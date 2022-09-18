package com.code.winter.ako.todolist.data.repository

import com.code.winter.ako.todolist.data.preference.*
import com.code.winter.ako.todolist.model.*
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getTasks(query: String, preference: TaskScreenPreference): Flow<List<Task>>

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun getCategories(): Flow<List<Category>>

    fun taskScreenPreferencesFlow(): Flow<TaskScreenPreference>

    suspend fun saveSortOrder(sortOrderTask: SortOrderTask)

    suspend fun saveHideCompleted(hideCompleted: Boolean)

    suspend fun saveCategoryName(categoryName: String)

    suspend fun deleteAllCompletedTask()

    fun getEditableCategories(sortOrderCategory: SortOrderCategory): Flow<List<ManageCategory>>

    suspend fun deleteCategoryByCategoryName(categoryName: String)

    suspend fun insertCategory(category: Category)

    suspend fun updateCategoryNameByCategoryName(newCategoryName: String, categoryName: String)

    fun mcScreenPreferenceFlow(): Flow<SortOrderCategory>

    suspend fun saveSortOrderCategory(sortOrderCategory: SortOrderCategory)
}