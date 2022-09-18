package com.code.winter.ako.todolist.model

import androidx.room.*

@Entity(tableName = "tbl_category")
data class Category(
    @PrimaryKey(autoGenerate = false)
    val categoryName: String,
    val editable: Boolean = true,
    val createdDate: Long = System.currentTimeMillis()
)
