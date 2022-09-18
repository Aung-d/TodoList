package com.code.winter.ako.todolist.util

import java.text.SimpleDateFormat
import java.util.*

fun <T> getDateByFormat(dateFormat: String, time: T): String =
    SimpleDateFormat(dateFormat, Locale.getDefault())
        .format(time)