package com.example.arrivyfirsttask

import android.app.Application
import com.example.arrivyfirsttask.constants.Constants
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name(Constants.REALM_DATABASE_NAME)
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .schemaVersion(4)
            .build()
        Realm.setDefaultConfiguration(config)
    }
}


