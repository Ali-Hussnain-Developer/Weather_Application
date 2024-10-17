package com.example.arrivyfirsttask

import android.app.Application
import com.example.arrivyfirsttask.constants.Constants
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Realm database for this application
        Realm.init(this)

        // Configure Realm with specific settings
        val config = RealmConfiguration.Builder()
            .name(Constants.REALM_DATABASE_NAME) // Set the name of the Realm database from constants
            .allowWritesOnUiThread(true) // Allow writing data on the UI thread (not recommended for large transactions)
            .allowQueriesOnUiThread(true) // Allow querying data on the UI thread
            .schemaVersion(4) // Set the schema version for migration purposes
            .build()

        // Set the default Realm configuration so that all instances use this setup
        Realm.setDefaultConfiguration(config)
    }
}


