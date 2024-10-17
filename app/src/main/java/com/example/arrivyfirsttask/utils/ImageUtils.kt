package com.example.arrivyfirsttask.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

// A singleton object that contains utility functions for image operations
object ImageUtils {

    // Function to load a weather icon into an ImageView using Glide
    fun loadWeatherIcon(context: Context, icon: String, imageView: ImageView) {
        // Construct the URL for the weather icon using the provided icon code
        val iconUrl = "https://openweathermap.org/img/wn/$icon@2x.png"

        // Use Glide to load the image from the constructed URL into the specified ImageView
        Glide.with(context) // Initialize Glide with the context
            .load(iconUrl) // Load the image from the URL
            .into(imageView) // Specify the ImageView where the image should be displayed
    }
}
