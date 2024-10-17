package com.example.arrivyfirsttask.model.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WeatherDataRealmModel : RealmObject() {

    @PrimaryKey
    var id: Int = 0                    // Primary key to uniquely identify each weather data entry
    var name: String = ""               // Name of the city or location
    var temperature: Double = 0.0       // Current temperature
    var maxTemperature: Double = 0.0    // Maximum temperature of the day
    var minTemperature: Double = 0.0    // Minimum temperature of the day
    var weatherStatusIcon: String = ""  // Icon representing the current weather status (e.g., sunny, rainy)
    var sunRiseTime: Long = 0           // Sunrise time (Unix timestamp)
    var sunSetTime: Long = 0            // Sunset time (Unix timestamp)
    var weatherHumidity: Int = 0        // Humidity percentage
    var windSpeed: Double = 0.0         // Wind speed in m/s

    // Custom string representation for easy debugging and logging
    override fun toString(): String {
        return "RealmModel(id=$id, name='$name', temperature=$temperature, maxTemperature=$maxTemperature, minTemperature=$minTemperature, weatherStatusIcon='$weatherStatusIcon', humidity=$weatherHumidity, windSpeed=$windSpeed, sunRiseTime='$sunRiseTime', sunSetTime='$sunSetTime')"
    }
}
