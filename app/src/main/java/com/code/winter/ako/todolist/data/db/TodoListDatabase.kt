package com.code.winter.ako.todolist.data.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.code.winter.ako.todolist.data.db.dao.*
import com.code.winter.ako.todolist.di.ApplicationScope
import com.code.winter.ako.todolist.model.*
import com.code.winter.ako.todolist.util.*
import kotlinx.coroutines.*
import javax.inject.*

@Database(entities = [Task::class, Category::class], version = 1)
abstract class TodoListDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    class DatabaseCallBack @Inject constructor(
        private val database: Provider<TodoListDatabase>,
        @ApplicationScope private val coroutineScope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = database.get().taskDao()
            val categoryDao = database.get().categoryDao()
            coroutineScope.launch {
                taskDao.insertTask(*defaultTaskList)
                categoryDao.insertCategory(*defaultCategoryList)
            }
        }
    }

    companion object {
        private val defaultTaskList = arrayOf(
            Task(taskName = "Wash the dishes"),
            Task(taskName = "Prepare dinner"),
            Task(taskName = "Watch movies"),
            Task(taskName = "Go shopping"),
            Task(taskName = "Do Homework"),
        )
        private val defaultCategoryList = arrayOf(
            Category(CATEGORY_NO, false),
            Category(CATEGORY_PERSONAL),
            Category(CATEGORY_WORK),
            Category(CATEGORY_SHOPPING),
            Category(CATEGORY_BIRTHDAY),
            Category(CATEGORY_WISHLIST),
        )
    }
}