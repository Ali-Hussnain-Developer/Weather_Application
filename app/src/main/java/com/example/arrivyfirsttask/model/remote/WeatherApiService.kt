package com.example.arrivyfirsttask.model.remote


import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Fetches the current weather data for a given city using city name and API key
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String, // City name query parameter
        @Query("appid") appId: String, // API key query parameter
        @Query("units") units: String = "metric" // Optional: Use metric units for temperature (default: Celsius)
    ): WeatherResponse

    // Fetches the hourly weather forecast for a specific location using latitude and longitude
    @GET("forecast")
    suspend fun getHourlyWeatherData(
        @Query("lat") latitude: Double, // Latitude query parameter
        @Query("lon") longitude: Double, // Longitude query parameter
        @Query("appid") appId: String, // API key query parameter
        @Query("units") units: String = "metric" // Optional: Use metric units for temperature (default: Celsius)
    ): HourlyWeatherResponse

}
