package com.code.winter.ako.todolist.presentation.task

import android.graphics.Color
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.code.winter.ako.todolist.databinding.TaskListItemBinding
import com.code.winter.ako.todolist.model.Task
import com.code.winter.ako.todolist.util.*

class TaskAdapter(private val listener: OnTaskItemClickListener) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {

    inner class TaskViewHolder(private val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClicked(task)
                    }
                }

                cbCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position).copy(completed = cbCompleted.isChecked)
                        listener.onCheckBoxClicked(task)
                    }
                }
            }
        }

        fun bindData(task: Task) {
            binding.apply {
                vPriority.setBackgroundResource(task.priority.getPriorityResource())
                vPriority.isVisible = !task.completed
                tvTask.text = task.taskName
                tvTask.paint.isStrikeThruText = task.completed
                cbCompleted.isChecked = task.completed
                tvTodoDate.text = task.dueDate.getStringDate()
                tvTime.text = task.time
                llDateTime.isVisible = !task.completed

                if (task.dueDate.overDueDate() == -1) {
                    setDateToRedColor()
                    setTimeToRedColor()
                } else if (task.dueDate.overDueDate() == 0 && task.time.overDueTime() == -1) {
                    setTimeToRedColor()
                }
            }
        }

        private fun setDateToRedColor() {
            binding.apply {
                tvTodoDate.setTextColor(Color.RED)
                ivTodoDate.setColorFilter(Color.RED)
            }
        }

        private fun setTimeToRedColor() {
            binding.apply {
                tvTime.setTextColor(Color.RED)
                ivTime.setColorFilter(Color.RED)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bindData(task)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.taskId == newItem.taskId

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }

    interface OnTaskItemClickListener {
        fun onItemClicked(task: Task)
        fun onCheckBoxClicked(task: Task)
    }

}