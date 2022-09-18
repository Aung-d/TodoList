package com.code.winter.ako.todolist.presentation.manage_category

import android.view.*
import androidx.recyclerview.widget.*
import com.code.winter.ako.todolist.databinding.CategoryListItemBinding
import com.code.winter.ako.todolist.model.ManageCategory

class ManageCategoryAdapter(private val listener: OnCategoryClickListener) :
    ListAdapter<ManageCategory, ManageCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    inner class CategoryViewHolder(private val binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        listener.onItemClicked(category)
                    }
                }
                ivDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        listener.onDeleteItemClicked(category)
                    }
                }
            }
        }

        fun bindData(category: ManageCategory) {
            binding.apply {
                tvCategoryName.text = category.categoryName
                tvTaskCount.text = category.taskCount.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bindData(category)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ManageCategory>() {
        override fun areItemsTheSame(oldItem: ManageCategory, newItem: ManageCategory) =
            oldItem.categoryName == newItem.categoryName

        override fun areContentsTheSame(oldItem: ManageCategory, newItem: ManageCategory) =
            oldItem == newItem
    }

    interface OnCategoryClickListener {
        fun onDeleteItemClicked(category: ManageCategory)
        fun onItemClicked(category: ManageCategory)
    }
}