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
    @GET("onecall")
    suspend fun getHourlyWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "current,minutely,daily,alerts",
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<HourlyWeatherResponse>
}
