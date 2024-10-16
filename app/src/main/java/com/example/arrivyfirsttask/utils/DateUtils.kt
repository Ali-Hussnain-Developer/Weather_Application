package com.example.arrivyfirsttask.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    fun getCurrentDate(): String {
        // Get the current date
        val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Define the desired format
        val formatter = DateTimeFormatter.ofPattern("MMMM, dd") // For example, "July, 21"

        // Format the current date
        return currentDate.format(formatter)
    }
}