package com.example.arrivyfirsttask.classes.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun convertTimestampToTime(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Convert to milliseconds
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Adjust format as needed
        return format.format(date)
    }
    fun convertUnixToTime(unixTimestamp: Long): String {
        val instant = Instant.ofEpochSecond(unixTimestamp)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault()) // Adjust the time zone as needed
        return formatter.format(instant)
    }
}