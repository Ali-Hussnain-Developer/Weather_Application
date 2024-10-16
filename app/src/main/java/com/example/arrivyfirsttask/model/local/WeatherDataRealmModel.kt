package com.example.arrivyfirsttask.model.local

import android.util.Log
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WeatherDataRealmModel : RealmObject(){
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var temperature: Double = 0.0
    var maxTemperature: Double = 0.0
    var minTemperature: Double = 0.0
    var weatherStatusIcon: String = ""
    var sunRiseTime: Long = 0
    var sunSetTime: Long = 0
    var weatherHumidity: Int = 0
    var windSpeed: Double = 0.0


    override fun toString(): String {
        return "RealmModel(id=$id, name='$name', temperature=$temperature, maxTemperature=$maxTemperature, minTemperature=$minTemperature, weatherStatusIcon='$weatherStatusIcon', humidity=${weatherHumidity}, windSpeed=$windSpeed, sunRiseTime='$sunRiseTime', sunSetTime='$sunSetTime')"
    }
}