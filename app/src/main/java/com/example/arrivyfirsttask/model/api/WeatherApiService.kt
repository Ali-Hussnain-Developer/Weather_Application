package com.example.arrivyfirsttask.model.api

import com.example.arrivyfirsttask.classes.data.HourlyWeatherResponse
import com.example.arrivyfirsttask.classes.data.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("forecast")
    suspend fun getHourlyWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") units: String = "metric"
    ): HourlyWeatherResponse // Assuming you have a separate model for hourly data
}