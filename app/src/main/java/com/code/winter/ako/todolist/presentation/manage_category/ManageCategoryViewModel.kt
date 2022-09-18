package com.code.winter.ako.todolist.presentation.manage_category

import androidx.lifecycle.*
import com.code.winter.ako.todolist.data.preference.SortOrderCategory
import com.code.winter.ako.todolist.data.repository.Repository
import com.code.winter.ako.todolist.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ManageCategoryViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories = repository.mcScreenPreferenceFlow().flatMapLatest { sortOrder ->
        repository.getEditableCategories(sortOrder)
    }

    fun onSortCategoryChanged(sortOrderCategory: SortOrderCategory) = viewModelScope.launch {
        repository.saveSortOrderCategory(sortOrderCategory)
    }

    fun onDeleteCategory(categoryName: String) = viewModelScope.launch {
        repository.deleteCategoryByCategoryName(categoryName)
    }

    fun onCreateCategory(categoryName: String) = viewModelScope.launch {
        repository.insertCategory(Category(categoryName))
    }

    fun onUpdateCategory(newCategoryName: String, categoryName: String) = viewModelScope.launch {
        repository.updateCategoryNameByCategoryName(newCategoryName, categoryName)
    }

}