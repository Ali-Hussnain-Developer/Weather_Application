package com.example.arrivyfirsttask.utils

import com.example.arrivyfirsttask.model.local.HourlyWeatherRealmModel
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem

object ListUtils {

    fun convertToHourlyWeatherItem(realmData: List<HourlyWeatherRealmModel>): List<HourlyWeatherItem> {
        return realmData.map { realmItem ->
            HourlyWeatherItem(
                time = realmItem.time,
                temperature = realmItem.temperature,
                icon = realmItem.weatherStatusIcon
            )
        }
    }
}