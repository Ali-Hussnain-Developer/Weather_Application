package com.example.arrivyfirsttask.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.databinding.ActivityMainBinding

// MainActivity is the entry point of the application, extending AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Late-initialized property for view binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set the content view to the root of the inflated binding
        setContentView(binding.root)
    }

    // Override onSupportNavigateUp to handle up navigation without an ActionBar
    override fun onSupportNavigateUp(): Boolean {
        // Get the NavHostFragment to access the NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        // Handle the up navigation, returning true if navigation was successful
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
