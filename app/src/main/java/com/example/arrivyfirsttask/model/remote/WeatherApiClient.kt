package com.example.arrivyfirsttask.model.remote

import com.example.arrivyfirsttask.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApiClient {

    // Lazy initialization of the WeatherApiService instance using Retrofit
    val instance: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Set the base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter to handle JSON serialization/deserialization
            .build() // Build the Retrofit instance
            .create(WeatherApiService::class.java) // Create and return the WeatherApiService implementation
    }
}
