package com.code.winter.ako.todolist.presentation.manage_category

import android.os.Bundle
import android.text.*
import android.text.InputFilter.LengthFilter
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.*
import androidx.core.view.setPadding
import androidx.lifecycle.*
import com.code.winter.ako.todolist.R
import com.code.winter.ako.todolist.data.preference.SortOrderCategory
import com.code.winter.ako.todolist.databinding.ActivityManageCategoryBinding
import com.code.winter.ako.todolist.model.ManageCategory
import com.code.winter.ako.todolist.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ManageCategoryActivity : AppCompatActivity(), ManageCategoryAdapter.OnCategoryClickListener {
    private val viewModel: ManageCategoryViewModel by viewModels()
    private lateinit var binding: ActivityManageCategoryBinding
    private lateinit var categoryAdapter: ManageCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.manage_categories)

        categoryAdapter = ManageCategoryAdapter(this)

        binding.apply {
            rvManageCategory.apply {
                adapter = categoryAdapter
                setHasFixedSize(true)
                val itemDecoration = TaskRecyclerViewDivider(22)
                addItemDecoration(itemDecoration)
            }
            fabAddCategory.setOnClickListener {
                createCategoryDialog()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collectLatest { categories ->
                    categoryAdapter.submitList(categories)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_manage_category, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.menuMSort) {
            sortOrderDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDeleteItemClicked(category: ManageCategory) {
        deleteCategoryDialog(category.categoryName)
    }

    override fun onItemClicked(category: ManageCategory) {
        createCategoryDialog(category.categoryName)
    }

    private fun sortOrderDialog() {
        val sortDialog = AlertDialog.Builder(this)
        sortDialog.setTitle(getString(R.string.sort_by))
        sortDialog.setItems(R.array.sort_order_category) { dialog, which ->
            val sortOrderCategory = if (which == 0) SortOrderCategory.BY_CATEGORY_NAME
            else SortOrderCategory.BY_CREATED_DATE
            viewModel.onSortCategoryChanged(sortOrderCategory)
            dialog.dismiss()
        }
        sortDialog.create().show()
    }

    private fun deleteCategoryDialog(categoryName: String) {
        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setTitle(getString(R.string.delete))
        deleteDialog.setMessage(getString(R.string.delete_category_message, categoryName))
        deleteDialog.setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
            viewModel.onDeleteCategory(categoryName)
            dialog.dismiss()
            showToast(getString(R.string.delete_category))
        }
        deleteDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        deleteDialog.create().show()
    }

    private fun createCategoryDialog(categoryName: String? = null) {
        val input = createEditText(categoryName ?: "")
        val container = FrameLayout(this)
        container.addView(input)

        val dialogTitle = if (categoryName == null) R.string.create_new_category
        else R.string.update_category
        val positiveButtonText = if (categoryName == null) R.string.create else R.string.update

        val createCategoryDialog = AlertDialog.Builder(this)
        createCategoryDialog.setView(container)
        createCategoryDialog.setTitle(getString(dialogTitle))
        createCategoryDialog.setPositiveButton(positiveButtonText, null)
        createCategoryDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val builder = createCategoryDialog.show()
        val positiveButton = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val newCategoryName = input.text.trim().toString()
            if (newCategoryName.isEmpty()) {
                showToast(getString(R.string.input_category_empty_message))
            } else {
                onSaveCategory(newCategoryName, categoryName)
                builder.dismiss()
            }
        }

    }

    private fun createEditText(categoryName: String): EditText {
        val input = EditText(this)
        input.setText(categoryName)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.filters = arrayOf<InputFilter>(LengthFilter(50))
        input.setBackgroundResource(R.drawable.edit_text_background)
        input.hint = getString(R.string.input_category_here)
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val spaceSize = 46
        layoutParams.setMargins(spaceSize, spaceSize, spaceSize, spaceSize)
        input.layoutParams = layoutParams
        input.setPadding(spaceSize)
        return input
    }

    private fun onSaveCategory(newCategoryName: String, categoryName: String?) {
        val message = if (categoryName != null) R.string.update_category
        else R.string.create_new_category
        if (categoryName != null) viewModel.onUpdateCategory(newCategoryName, categoryName)
        else viewModel.onCreateCategory(newCategoryName)
        showToast(getString(message))
    }
}