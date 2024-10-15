package com.example.arrivyfirsttask.classes.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun convertTimestampToTime(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Convert to milliseconds
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Adjust format as needed
        return format.format(date)
    }
}