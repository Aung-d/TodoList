package com.code.winter.ako.todolist.presentation.add_edit

import androidx.lifecycle.*
import com.code.winter.ako.todolist.R
import com.code.winter.ako.todolist.data.repository.Repository
import com.code.winter.ako.todolist.model.Task
import com.code.winter.ako.todolist.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val task = savedStateHandle.get<Task>(EXTRA_KEY_TASK)

    val isScreenEditMode = task != null

    private val _displayToastShareFlow = MutableSharedFlow<Int>()
    val displayToastShareFlow = _displayToastShareFlow.asSharedFlow()

    private val _navigateBackShareFlow = MutableSharedFlow<Boolean>()
    val navigateBackShareFlow = _navigateBackShareFlow.asSharedFlow()

    val createdDate = task?.createdDate ?: 0

    private var _taskName = task?.taskName ?: ""
    val taskName get() = _taskName

    fun onTaskChanged(taskName: String) {
        _taskName = taskName
    }

    private var _priorityIndex: Int = task?.priority ?: PRIORITY_NONE
    val priorityIndex get() = _priorityIndex

    fun setPriorityIndex(index: Int) {
        _priorityIndex = index
    }

    private var _dueDate = task?.dueDate ?: System.currentTimeMillis()
    val dueDate get() = _dueDate

    fun onDateChanged(dueDate: Long) {
        _dueDate = dueDate
    }

    private var _time = task?.time ?: System.currentTimeMillis().plus(10800000L).getStringTime()
    val time get() = _time

    fun onTimeChanged(time: String) {
        _time = time
    }

    private var _categoryName = task?.categoryName ?: CATEGORY_NO
    val categoryName get() = _categoryName

    fun onCategoryChanged(category: String) {
        _categoryName = category
    }

    private var _categories = arrayOf<String>()
    val categories get() = _categories

    init {
        viewModelScope.launch {
            repository.getCategories().collectLatest { categoryList ->
                _categories = categoryList.map { it.categoryName }.toTypedArray()
            }
        }
    }

    fun onSaveTask() {
        if (taskName.trim().isEmpty()) {
            viewModelScope.launch {
                _displayToastShareFlow.emit(R.string.input_task_empty_message)
            }
            return
        }
        if (task != null) {
            updateTask()
        } else {
            createTask()
        }
        navigateBack()
    }

    fun onDeleteTask() = viewModelScope.launch {
        task?.let {
            repository.deleteTask(it)
        }
        navigateBack()
        _displayToastShareFlow.emit(R.string.delete_task)
    }

    fun onFinishedCategory() {
        updateTask(true)
        navigateBack()
    }

    private fun navigateBack() = viewModelScope.launch {
        _navigateBackShareFlow.emit(true)
    }

    private fun updateTask(isDone: Boolean = false) = viewModelScope.launch {
        val updateTask = task!!.copy(
            taskName = taskName,
            priority = priorityIndex,
            dueDate = dueDate,
            time = time,
            categoryName = categoryName,
            completed = isDone
        )
        repository.updateTask(updateTask)
        val message = if (isDone) R.string.finished_task else R.string.update_task
        _displayToastShareFlow.emit(message)
    }

    private fun createTask() = viewModelScope.launch {
        val newTask = Task(
            taskName = taskName,
            priority = priorityIndex,
            dueDate = dueDate,
            time = time,
            categoryName = categoryName
        )
        repository.insertTask(newTask)
        _displayToastShareFlow.emit(R.string.create_new_task)
    }

}