package com.example.arrivyfirsttask.utils

import com.example.arrivyfirsttask.model.local.HourlyWeatherRealmModel
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem

// A singleton object that contains utility functions for list operations
object ListUtils {

    // Function to convert a list of HourlyWeatherRealmModel to a list of HourlyWeatherItem
    fun convertToHourlyWeatherItem(realmData: List<HourlyWeatherRealmModel>): List<HourlyWeatherItem> {
        // Use the map function to transform each HourlyWeatherRealmModel into HourlyWeatherItem
        return realmData.map { realmItem ->
            // Create a new HourlyWeatherItem using the properties from the realmItem
            HourlyWeatherItem(
                time = realmItem.time, // Extract time from the realmItem
                temperature = realmItem.temperature, // Extract temperature from the realmItem
                icon = realmItem.weatherStatusIcon // Extract weather icon from the realmItem
            )
        }
    }
}
