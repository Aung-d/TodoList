package com.code.winter.ako.todolist.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TaskRecyclerViewDivider(private val itemOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(itemOffset, itemOffset, itemOffset, 0)
    }
}