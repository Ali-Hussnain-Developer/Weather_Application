package com.example.arrivyfirsttask.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.databinding.ActivityMainBinding

// MainActivity is the entry point of the application, extending AppCompatActivity
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

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
