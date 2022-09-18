package com.code.winter.ako.todolist.di

import android.content.Context
import androidx.room.Room
import com.code.winter.ako.todolist.data.db.TodoListDatabase
import com.code.winter.ako.todolist.data.repository.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
        callback: TodoListDatabase.DatabaseCallBack
    ): TodoListDatabase =
        Room.databaseBuilder(context, TodoListDatabase::class.java, "todo_list.db")
            .addCallback(callback)
            .build()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideTaskRepository(
        taskRepositoryImpl: RepositoryImpl
    ): Repository = taskRepositoryImpl
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope