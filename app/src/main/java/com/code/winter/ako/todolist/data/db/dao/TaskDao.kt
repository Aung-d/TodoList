package com.code.winter.ako.todolist.data.db.dao

import androidx.room.*
import com.code.winter.ako.todolist.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(vararg task: Task)

    @Query("SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0) AND categoryName = :categoryName AND taskName LIKE '%' || :query || '%' ORDER BY dueDate")
    fun getTasksSortedByDueDate(
        query: String,
        hideCompleted: Boolean,
        categoryName: String
    ): Flow<List<Task>>

    @Query("SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0) AND taskName LIKE '%' || :query || '%' ORDER BY dueDate")
    fun getAllTasksSortedByDueDate(query: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0) AND categoryName = :categoryName AND taskName LIKE '%' || :query || '%' ORDER BY createdDate DESC")
    fun getTasksSortedByCreatedDate(
        query: String,
        hideCompleted: Boolean,
        categoryName: String
    ): Flow<List<Task>>

    @Query("SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0) AND taskName LIKE '%' || :query || '%' ORDER BY createdDate DESC")
    fun getAllTasksSortedByCreatedDate(query: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0)  AND categoryName = :categoryName AND taskName LIKE '%' || :query || '%' ORDER BY taskName")
    fun getTasksSortedByName(
        query: String,
        hideCompleted: Boolean,
        categoryName: String
    ): Flow<List<Task>>

    @Query("SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0) AND taskName LIKE '%' || :query || '%' ORDER BY taskName")
    fun getAllTasksSortedByName(query: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("DELETE FROM tbl_task WHERE completed = 1")
    suspend fun deleteAllCompletedTask()

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tbl_task WHERE categoryName = :categoryName")
    suspend fun deleteTasksByCategoryName(categoryName: String)
}