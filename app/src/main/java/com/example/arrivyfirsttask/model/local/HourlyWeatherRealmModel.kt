package com.example.arrivyfirsttask.model.local
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HourlyWeatherRealmModel : RealmObject() {

    @PrimaryKey
    var id: Int = 0                      // Unique identifier for each record
    var time: String = ""                // Time of the weather data (e.g., "14:00")
    var temperature: String = ""          // Temperature as a string (e.g., "32.99°C")
    var weatherStatusIcon: String = ""   // Weather icon code representing current weather

    // Custom string representation for easy debugging and logging
    override fun toString(): String {
        return "HourlyWeatherRealmModel(id=$id, time='$time', temperature='$temperature', weatherStatusIcon='$weatherStatusIcon')"
    }
}
