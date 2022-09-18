package com.code.winter.ako.todolist.data.repository

import com.code.winter.ako.todolist.data.db.TodoListDatabase
import com.code.winter.ako.todolist.data.preference.*
import com.code.winter.ako.todolist.model.*
import com.code.winter.ako.todolist.util.CATEGORY_ALL
import kotlinx.coroutines.flow.Flow
import javax.inject.*

@Singleton
class RepositoryImpl @Inject constructor(
    private val database: TodoListDatabase,
    private val dataStorePreferenceManager: DataStorePreferenceManager
) : Repository {

    override fun getTasks(query: String, preference: TaskScreenPreference) =
        when (preference.sortOrderTask) {
            SortOrderTask.BY_DUE_DATE -> {
                if (preference.categoryName == CATEGORY_ALL)
                    database.taskDao()
                        .getAllTasksSortedByDueDate(query, preference.hideCompleted)
                else database.taskDao()
                    .getTasksSortedByDueDate(
                        query,
                        preference.hideCompleted,
                        preference.categoryName
                    )
            }
            SortOrderTask.BY_CREATED_DATE -> {
                if (preference.categoryName == CATEGORY_ALL)
                    database.taskDao()
                        .getAllTasksSortedByCreatedDate(query, preference.hideCompleted)
                else database.taskDao()
                    .getTasksSortedByCreatedDate(
                        query,
                        preference.hideCompleted,
                        preference.categoryName
                    )
            }
            SortOrderTask.BY_TASK_NAME -> {
                if (preference.categoryName == CATEGORY_ALL)
                    database.taskDao()
                        .getAllTasksSortedByName(query, preference.hideCompleted)
                else database.taskDao()
                    .getTasksSortedByName(query, preference.hideCompleted, preference.categoryName)
            }
        }

    override suspend fun insertTask(task: Task) =
        database.taskDao().insertTask(task)

    override suspend fun updateTask(task: Task) =
        database.taskDao().updateTask(task)

    override suspend fun deleteTask(task: Task) =
        database.taskDao().deleteTask(task)

    override fun getCategories(): Flow<List<Category>> =
        database.categoryDao().getCategories()

    override fun taskScreenPreferencesFlow(): Flow<TaskScreenPreference> =
        dataStorePreferenceManager.taskScreenPreferencesFlow

    override suspend fun saveSortOrder(sortOrderTask: SortOrderTask) =
        dataStorePreferenceManager.saveSortOrderTask(sortOrderTask)

    override suspend fun saveHideCompleted(hideCompleted: Boolean) =
        dataStorePreferenceManager.saveHideCompleted(hideCompleted)

    override suspend fun saveCategoryName(categoryName: String) =
        dataStorePreferenceManager.saveCategoryName(categoryName)

    override suspend fun deleteAllCompletedTask() =
        database.taskDao().deleteAllCompletedTask()

    override fun getEditableCategories(sortOrderCategory: SortOrderCategory): Flow<List<ManageCategory>> =
        when (sortOrderCategory) {
            SortOrderCategory.BY_CREATED_DATE -> database.categoryDao()
                .getEditableCategoriesSortedByCreateDate()
            SortOrderCategory.BY_CATEGORY_NAME -> database.categoryDao()
                .getEditableCategoriesSortedByCategoryName()
        }

    override suspend fun deleteCategoryByCategoryName(categoryName: String) {
        database.categoryDao().deleteCategoryByCategoryName(categoryName)
        database.taskDao().deleteTasksByCategoryName(categoryName)
    }

    override suspend fun insertCategory(category: Category) =
        database.categoryDao().insertCategory(category)

    override suspend fun updateCategoryNameByCategoryName(
        newCategoryName: String,
        categoryName: String
    ) = database.categoryDao().updateCategoryNameByCategoryName(newCategoryName, categoryName)

    override fun mcScreenPreferenceFlow(): Flow<SortOrderCategory> =
        dataStorePreferenceManager.mcScreenPreferenceFlow

    override suspend fun saveSortOrderCategory(sortOrderCategory: SortOrderCategory) =
        dataStorePreferenceManager.saveSortOrderCategory(sortOrderCategory)
}