package com.code.winter.ako.todolist.presentation.task

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.*
import androidx.recyclerview.widget.*
import com.code.winter.ako.todolist.R
import com.code.winter.ako.todolist.data.preference.SortOrderTask
import com.code.winter.ako.todolist.databinding.ActivityTaskBinding
import com.code.winter.ako.todolist.model.Task
import com.code.winter.ako.todolist.presentation.add_edit.AddEditTaskActivity
import com.code.winter.ako.todolist.presentation.manage_category.ManageCategoryActivity
import com.code.winter.ako.todolist.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskActivity : AppCompatActivity(), TaskAdapter.OnTaskItemClickListener {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: ActivityTaskBinding
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskAdapter = TaskAdapter(this)

        lifecycleScope.launch {
            viewModel.taskScreenPreferencesFlow.collectLatest { preferences ->
                supportActionBar!!.title = preferences.categoryName
            }
        }

        binding.apply {
            rvTask.apply {
                adapter = taskAdapter
                setHasFixedSize(true)
                val itemDecoration = TaskRecyclerViewDivider(22)
                addItemDecoration(itemDecoration)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onSwipedDeleteTask(task)
                    showToast(getString(R.string.delete_task))
                }
            }).attachToRecyclerView(rvTask)

            fabAddTask.setOnClickListener {
                svTask.clearFocus()
                val intent = Intent(this@TaskActivity, AddEditTaskActivity::class.java)
                startActivity(intent)
            }

            svTask.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.onQueryChanged(newText ?: "")
                    return true
                }
            })
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksFlow.collectLatest { tasks ->
                    taskAdapter.submitList(tasks)
                }
            }
        }
    }

    override fun onItemClicked(task: Task) {
        if (!task.completed) {
            binding.svTask.clearFocus()
            val intent = Intent(this@TaskActivity, AddEditTaskActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_KEY_TASK, task)
            intent.putExtras(bundle)
            startActivity(intent)
        } else {
            showToast(getString(R.string.task_completed))
        }
    }

    override fun onCheckBoxClicked(task: Task) {
        viewModel.onTaskCheckedChanged(task)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_task_screen, menu)

        lifecycleScope.launch {
            val hideCompleted = viewModel.taskScreenPreferencesFlow.first().hideCompleted
            menu?.findItem(R.id.menuHideCompleted)?.isChecked = hideCompleted
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuCategory -> categoryDialog()
            R.id.menuSort -> sortOrderDialog()
            R.id.menuHideCompleted -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedTask(item.isChecked)
            }
            R.id.menuDeleteCompleted -> deleteAllCompletedDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortOrderDialog() {
        val sortDialog = AlertDialog.Builder(this)
        sortDialog.setTitle(getString(R.string.sort_by))
        sortDialog.setItems(R.array.sort_order_task) { dialog, which ->
            val sortOrderTask = when (which) {
                0 -> SortOrderTask.BY_TASK_NAME
                1 -> SortOrderTask.BY_DUE_DATE
                else -> SortOrderTask.BY_CREATED_DATE
            }
            viewModel.onSortTaskChanged(sortOrderTask)
            dialog.dismiss()
        }
        sortDialog.create().show()
    }

    private fun categoryDialog() {
        val categoryDialog = AlertDialog.Builder(this)
        categoryDialog.setTitle(getString(R.string.choose_category))
        categoryDialog.setItems(viewModel.categories) { dialog, which ->
            if (which == viewModel.categories.size - 1) {
                binding.svTask.clearFocus()
                startActivity(Intent(this, ManageCategoryActivity::class.java))
            } else {
                viewModel.onCategoryChanged(viewModel.categories[which])
            }
            dialog.dismiss()
        }
        categoryDialog.create().show()
    }

    private fun deleteAllCompletedDialog() {
        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setTitle(getString(R.string.delete))
        deleteDialog.setMessage(getString(R.string.delete_completed_dialog_message))
        deleteDialog.setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
            viewModel.onDeleteAllCompleted()
            dialog.dismiss()
            showToast(getString(R.string.delete_all_completed_task))
        }
        deleteDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        deleteDialog.create().show()
    }
}