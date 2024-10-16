package com.example.arrivyfirsttask

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("Myrealm.realm")
            .allowWritesOnUiThread(false)
            .allowQueriesOnUiThread(false)
            .schemaVersion(4)
            .build()
        Realm.setDefaultConfiguration(config)
    }
}


