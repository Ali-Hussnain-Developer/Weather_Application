package com.example.arrivyfirsttask.model.local
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HourlyWeatherRealmModel : RealmObject() {
    @PrimaryKey
    var id: Int = 0 // Unique identifier for each record
    var time: String = "" // Time of the weather data
    var temperature: String = "" // Temperature, e.g., "32.99°C"
    var weatherStatusIcon: String = "" // Weather icon code

    override fun toString(): String {
        return "HourlyWeatherRealmModel(id=$id, time='$time', temperature='$temperature', weatherStatusIcon='$weatherStatusIcon')"
    }
}
