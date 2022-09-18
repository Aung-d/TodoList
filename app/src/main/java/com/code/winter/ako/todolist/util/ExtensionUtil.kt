package com.code.winter.ako.todolist.util

import android.content.Context
import android.widget.Toast
import com.code.winter.ako.todolist.R
import java.text.SimpleDateFormat
import java.util.*

fun Int.getPriorityColor() =
    when (this) {
        PRIORITY_HIGH -> R.color.red
        PRIORITY_MEDIUM -> R.color.orange
        PRIORITY_LOW -> R.color.green
        else -> R.color.grey
    }

fun Int.getPriorityResource() =
    when (this) {
        PRIORITY_HIGH -> R.drawable.priority_high
        PRIORITY_MEDIUM -> R.drawable.priority_medium
        PRIORITY_LOW -> R.drawable.priority_low
        else -> 0
    }

fun Long.getStringDate(): String = getDateByFormat(DEFAULT_DATE_FORMAT, this)

fun Long.getStringTime(): String = getDateByFormat(DEFAULT_TIME_FORMAT, this)

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.compareWithCurrentDate(dateFormat: String): Int {
    val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
    val date = sdf.parse(this)
    val currentDate = sdf.parse(getDateByFormat(dateFormat, System.currentTimeMillis()))
    return date?.compareTo(currentDate) ?: -1
}

fun Long.overDueDate() = this.getStringDate().compareWithCurrentDate(DEFAULT_DATE_FORMAT)

fun String.overDueTime() = this.compareWithCurrentDate(DEFAULT_TIME_FORMAT)







