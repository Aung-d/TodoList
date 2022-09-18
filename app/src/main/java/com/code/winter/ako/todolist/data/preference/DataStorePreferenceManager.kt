package com.code.winter.ako.todolist.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.code.winter.ako.todolist.util.CATEGORY_ALL
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "task_preference")

enum class SortOrderTask { BY_TASK_NAME, BY_DUE_DATE, BY_CREATED_DATE }

enum class SortOrderCategory { BY_CATEGORY_NAME, BY_CREATED_DATE }

data class TaskScreenPreference(
    val sortOrderTask: SortOrderTask,
    val hideCompleted: Boolean,
    val categoryName: String
)

@Singleton
class DataStorePreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val taskScreenPreferencesFlow = context.dataStore.data.map { preferences ->
        val sortOrderTask = SortOrderTask.valueOf(
            preferences[SORT_ORDER_TASK_PREF_KEY] ?: SortOrderTask.BY_DUE_DATE.name
        )
        val hideCompleted = preferences[HIDE_COMPLETED_PREF_KEY] ?: false

        val categoryName = preferences[CATEGORY_NAME_PREF_KEY] ?: CATEGORY_ALL
        TaskScreenPreference(sortOrderTask, hideCompleted, categoryName)
    }

    val mcScreenPreferenceFlow = context.dataStore.data.map { preferences ->
        SortOrderCategory.valueOf(
            preferences[SORT_ORDER_CATEGORY_PREF_KEY] ?: SortOrderCategory.BY_CREATED_DATE.name
        )
    }

    suspend fun saveSortOrderTask(sortOrderTask: SortOrderTask) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER_TASK_PREF_KEY] = sortOrderTask.name
        }
    }

    suspend fun saveHideCompleted(hideCompleted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HIDE_COMPLETED_PREF_KEY] = hideCompleted
        }
    }

    suspend fun saveSortOrderCategory(sortOrderCategory: SortOrderCategory) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER_CATEGORY_PREF_KEY] = sortOrderCategory.name
        }
    }

    suspend fun saveCategoryName(categoryName: String) {
        context.dataStore.edit { preferences ->
            preferences[CATEGORY_NAME_PREF_KEY] = categoryName
        }
    }

    companion object {
        val SORT_ORDER_TASK_PREF_KEY = stringPreferencesKey("sort_order_task")
        val HIDE_COMPLETED_PREF_KEY = booleanPreferencesKey("hide_completed")
        val CATEGORY_NAME_PREF_KEY = stringPreferencesKey("category_name")
        val SORT_ORDER_CATEGORY_PREF_KEY = stringPreferencesKey("sort_order_category")
    }

}