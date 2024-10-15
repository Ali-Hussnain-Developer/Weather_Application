package com.example.arrivyfirsttask.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.arrivyfirsttask.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    private fun initialization() {
        lifecycleScope.launch {
            delay(20000) // Delay for 3 seconds
            // Start the HomeActivity after the delay
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent) // Navigate to HomeActivity
            finish() // Close the SplashActivity
        }
    }
}