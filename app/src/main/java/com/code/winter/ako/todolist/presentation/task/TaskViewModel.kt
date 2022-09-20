package com.code.winter.ako.todolist.presentation.task

import androidx.lifecycle.*
import com.code.winter.ako.todolist.data.preference.SortOrderTask
import com.code.winter.ako.todolist.data.repository.Repository
import com.code.winter.ako.todolist.model.Task
import com.code.winter.ako.todolist.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val taskScreenPreferencesFlow = repository.taskScreenPreferencesFlow()

    private var _categories = arrayOf<String>()
    val categories get() = _categories

    init {
        viewModelScope.launch {
            repository.getCategories().collectLatest { categoryList ->
                val mutableList = mutableListOf<String>()
                mutableList.add(CATEGORY_ALL)
                mutableList.addAll(categoryList.map { it.categoryName })
                mutableList.add(CATEGORY_MANAGE)
                _categories = mutableList.toTypedArray()
            }
        }
    }

    private val searchQueryFlow = MutableStateFlow("")

    fun onQueryChanged(query: String) = viewModelScope.launch {
        searchQueryFlow.emit(query)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksFlow = combine(searchQueryFlow, taskScreenPreferencesFlow) { query, preference ->
        Pair(query, preference)
    }.flatMapLatest { (query, preference) ->
        repository.getTasks(query, preference)
    }

    fun onTaskCheckedChanged(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun onSwipedDeleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun onSortTaskChanged(sortOrderTask: SortOrderTask) = viewModelScope.launch {
        repository.saveSortOrder(sortOrderTask)
    }

    fun onHideCompletedTask(hideCompleted: Boolean) = viewModelScope.launch {
        repository.saveHideCompleted(hideCompleted)
    }

    fun onCategoryChanged(categoryName: String) = viewModelScope.launch {
        repository.saveCategoryName(categoryName)
    }

    fun onDeleteAllCompleted() = viewModelScope.launch {
        repository.deleteAllCompletedTask()
    }
}