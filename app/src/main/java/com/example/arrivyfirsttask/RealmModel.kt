package com.example.arrivyfirsttask

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmModel : RealmObject(){
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var temperature: Double = 0.0
    var maxTemperature: Double = 0.0
    var minTemperature: Double = 0.0
    var weatherStatusIcon: String = ""

    override fun toString(): String {
        return "RealmModel(id=$id, name='$name', temperature=$temperature, maxTemperature=$maxTemperature, minTemperature=$minTemperature, weatherStatusIcon='$weatherStatusIcon')"
    }
}