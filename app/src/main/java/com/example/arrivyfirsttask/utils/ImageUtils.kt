package com.example.arrivyfirsttask.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageUtils {
    fun loadWeatherIcon(context: Context, icon: String, imageView: ImageView) {
        // Construct the URL for the weather icon
        val iconUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
        Glide.with(context)
            .load(iconUrl)
            .into(imageView)
    }
}