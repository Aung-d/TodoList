package com.code.winter.ako.todolist.presentation.add_edit

import android.app.*
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.code.winter.ako.todolist.R
import com.code.winter.ako.todolist.databinding.ActivityAddEditTaskBinding
import com.code.winter.ako.todolist.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddEditTaskActivity : AppCompatActivity() {
    private val viewModel: AddEditTaskViewModel by viewModels()
    private lateinit var binding: ActivityAddEditTaskBinding
    private val priorityList by lazy {
        resources.getStringArray(R.array.priority)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val title = if (viewModel.isScreenEditMode) R.string.edit_task else R.string.create_new_task
        supportActionBar!!.title = getString(title)

        lifecycleScope.launch {
            viewModel.navigateBackShareFlow.collectLatest { isNavigate ->
                if (isNavigate) {
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.displayToastShareFlow.collectLatest { message ->
                showToast(getString(message))
            }
        }

        setPriorityText()
        binding.apply {
            etTask.setText(viewModel.taskName)
            etTask.addTextChangedListener {
                viewModel.onTaskChanged(it.toString())
            }
            tvDate.text = viewModel.dueDate.getStringDate()
            tvTime.text = viewModel.time
            setOverDueColor()
            tvCategory.text = viewModel.categoryName

            tvCreatedDate.text = viewModel.createdDate.getStringDate()
            llCreatedDate.isVisible = viewModel.isScreenEditMode

            llPriority.setOnClickListener {
                priorityDialog()
            }
            llPickDate.setOnClickListener {
                datePickerDialog()
            }
            llCategory.setOnClickListener {
                categoryDialog()
            }
            llPickTime.setOnClickListener {
                timePickerDialog()
            }
            bCancel.setOnClickListener {
                finish()
            }
            bDeleteTask.setOnClickListener {
                deleteTaskDialog()
            }
            bDone.setOnClickListener {
                viewModel.onFinishedCategory()
            }
            bDeleteTask.isVisible = viewModel.isScreenEditMode
            bDone.isVisible = viewModel.isScreenEditMode
        }

    }

    private fun setOverDueColor() {
        binding.apply {
            if (viewModel.dueDate.overDueDate() == -1) {
                tvDate.setTextColor(Color.RED)
                tvTime.setTextColor(Color.RED)
            } else if (viewModel.dueDate.overDueDate() == 0 && viewModel.time.overDueTime() == -1) {
                tvTime.setTextColor(Color.RED)
            } else {
                tvDate.setTextColor(Color.BLACK)
                tvTime.setTextColor(Color.BLACK)
            }
        }
    }

    private fun setPriorityText() {
        binding.tvPriority.apply {
            text = priorityList[viewModel.priorityIndex]
            setTextColor(getColor(viewModel.priorityIndex.getPriorityColor()))
        }
    }

    private fun priorityDialog() {
        val priorityDialog = AlertDialog.Builder(this)
        priorityDialog.setTitle(getString(R.string.select_a_priority))
        priorityDialog.setSingleChoiceItems(
            priorityList,
            viewModel.priorityIndex
        ) { dialog, index ->
            viewModel.setPriorityIndex(index)
            setPriorityText()
            dialog.dismiss()
        }
        priorityDialog.create().show()
    }

    private fun deleteTaskDialog() {
        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setTitle(getString(R.string.delete))
        deleteDialog.setMessage(getString(R.string.delete_task_message))
        deleteDialog.setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
            viewModel.onDeleteTask()
            dialog.dismiss()
        }
        deleteDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        deleteDialog.create().show()
    }

    private fun categoryDialog() {
        val priorityDialog = AlertDialog.Builder(this)
        priorityDialog.setTitle(getString(R.string.select_a_category))
        val selectedIndex = viewModel.categories.indexOf(viewModel.categoryName)
        priorityDialog.setSingleChoiceItems(viewModel.categories, selectedIndex) { dialog, index ->
            viewModel.onCategoryChanged(viewModel.categories[index])
            binding.tvCategory.text = viewModel.categoryName
            dialog.dismiss()
        }
        priorityDialog.create().show()
    }

    private fun datePickerDialog() {
        val calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mMonth = calendar.get(Calendar.MONTH)
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                viewModel.onDateChanged(calendar.timeInMillis)
                binding.tvDate.text = calendar.timeInMillis.getStringDate()
                setOverDueColor()
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun timePickerDialog() {
        val calendar = Calendar.getInstance()
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                binding.tvTime.text = calendar.timeInMillis.getStringTime()
                viewModel.onTimeChanged(calendar.timeInMillis.getStringTime())
                setOverDueColor()
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit_screen, menu)
        val menuTitle = if (viewModel.isScreenEditMode) R.string.update else R.string.create
        menu?.findItem(R.id.menuCreate)?.setTitle(menuTitle)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.menuCreate) {
            viewModel.onSaveTask()
        }
        return super.onOptionsItemSelected(item)
    }

}