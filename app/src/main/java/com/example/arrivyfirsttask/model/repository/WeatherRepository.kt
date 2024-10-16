package com.example.arrivyfirsttask.model.repository

import com.example.arrivyfirsttask.classes.constants.Constants
import com.example.arrivyfirsttask.classes.data.HourlyWeatherResponse
import com.example.arrivyfirsttask.classes.data.WeatherResponse
import com.example.arrivyfirsttask.classes.sealed.ApiResult
import com.example.arrivyfirsttask.model.api.WeatherApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepository(private val weatherApi: WeatherApiService) {
    suspend fun fetchWeatherByCity(city: String): Flow<ApiResult<WeatherResponse>> = flow {
        emit(ApiResult.Loading) // Emit loading state
        try {
            val response = weatherApi.getWeatherByCity(city, Constants.API_KEY)
            emit(ApiResult.Success(response)) // Emit success state
        } catch (e: HttpException) {
            emit(ApiResult.Error(e)) // Emit error state
        } catch (e: IOException) {
            emit(ApiResult.Error(e)) // Emit error state
        }
    }

    suspend fun fetchHourlyWeatherData(latitude: Double, longitude: Double): Flow<ApiResult<HourlyWeatherResponse>> = flow {
        emit(ApiResult.Loading) // Emit loading state
        try {
            val response = weatherApi.getHourlyWeatherData(latitude, longitude, Constants.API_KEY)
            emit(ApiResult.Success(response)) // Emit success state
        } catch (e: HttpException) {
            emit(ApiResult.Error(e)) // Emit error state
        } catch (e: IOException) {
            emit(ApiResult.Error(e)) // Emit error state
        }
    }

}
