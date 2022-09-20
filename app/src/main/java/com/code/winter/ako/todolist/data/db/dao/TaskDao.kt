package com.code.winter.ako.todolist.data.db.dao

import androidx.room.*
import com.code.winter.ako.todolist.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(vararg task: Task)

    @Query(
        """
        SELECT * FROM tbl_task WHERE (completed != :hideCompleted OR completed = 0) AND
        categoryName = CASE WHEN :categoryName == 'All tasks' THEN categoryName ELSE :categoryName END
        AND taskName LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN :sortOrder = 'BY_TASK_NAME' THEN UPPER(taskName) END ASC, 
            CASE  WHEN :sortOrder = 'BY_DUE_DATE' THEN dueDate END,
            CASE  WHEN :sortOrder = 'BY_CREATED_DATE' THEN createdDate END DESC
    """
    )
    fun getTasks(
        query: String,
        hideCompleted: Boolean,
        categoryName: String,
        sortOrder: String
    ): Flow<List<Task>>

    @Query("DELETE FROM tbl_task WHERE completed = 1")
    suspend fun deleteAllCompletedTask()

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tbl_task WHERE categoryName = :categoryName")
    suspend fun deleteTasksByCategoryName(categoryName: String)

    @Query("UPDATE tbl_task SET categoryName =:newCategoryName WHERE categoryName = :categoryName")
    suspend fun updateTaskCategoryNameByCategoryName(newCategoryName: String, categoryName: String)
}