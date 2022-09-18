package com.code.winter.ako.todolist.model

import android.os.Parcelable
import androidx.room.*
import com.code.winter.ako.todolist.util.*
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "tbl_task")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val taskName: String,
    val completed: Boolean = false,
    val priority: Int = PRIORITY_NONE,
    val categoryName: String = CATEGORY_NO,
    val createdDate: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis(),
    val time: String = System.currentTimeMillis().plus(10800000L).getStringTime()
) : Parcelable
