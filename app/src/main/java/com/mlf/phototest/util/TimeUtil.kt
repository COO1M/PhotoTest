package com.mlf.phototest.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    val dateTimeFormat by lazy {
        SimpleDateFormat("yyyy-M-d H:m:s", Locale.getDefault())
    }

    fun millisFormat(millis: Long): String {
        return dateTimeFormat.format(millis * 1000)
    }
}