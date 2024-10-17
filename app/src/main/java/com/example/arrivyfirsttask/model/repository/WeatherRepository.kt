package com.example.arrivyfirsttask.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.arrivyfirsttask.constants.Constants
import com.example.arrivyfirsttask.model.local.HourlyWeatherRealmModel
import com.example.arrivyfirsttask.model.local.WeatherDataRealmModel
import com.example.arrivyfirsttask.model.remote.HourlyWeatherResponse
import com.example.arrivyfirsttask.model.remote.WeatherResponse
import com.example.arrivyfirsttask.sealed.ApiResult
import com.example.arrivyfirsttask.model.remote.WeatherApiService
import io.realm.Realm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepository(private val weatherApi: WeatherApiService) {
    // Realm instance for interacting with the database
    private val realm: Realm = Realm.getDefaultInstance()

    // Function to fetch weather data by city name and emit the result using Flow
    fun fetchWeatherByCity(city: String): Flow<ApiResult<WeatherResponse>> = flow {
        emit(ApiResult.Loading) // Emit loading state before starting the network call
        try {
            // Perform network request to fetch weather data by city name
            val response = weatherApi.getWeatherByCity(city, Constants.API_KEY)
            emit(ApiResult.Success(response)) // Emit success state with the response
        } catch (e: HttpException) {
            emit(ApiResult.Error(e)) // Emit error state if an HTTP exception occurs
        } catch (e: IOException) {
            emit(ApiResult.Error(e)) // Emit error state if an IO exception occurs (e.g., network issues)
        }
    }

    // Function to fetch hourly weather data by latitude and longitude using Flow
    fun fetchHourlyWeatherData(latitude: Double, longitude: Double): Flow<ApiResult<HourlyWeatherResponse>> = flow {
        emit(ApiResult.Loading) // Emit loading state before starting the network call
        try {
            // Perform network request to fetch hourly weather data
            val response = weatherApi.getHourlyWeatherData(latitude, longitude, Constants.API_KEY)
            emit(ApiResult.Success(response)) // Emit success state with the response
        } catch (e: HttpException) {
            emit(ApiResult.Error(e)) // Emit error state if an HTTP exception occurs
        } catch (e: IOException) {
            emit(ApiResult.Error(e)) // Emit error state if an IO exception occurs
        }
    }

    // Function to retrieve weather data stored in Realm and return it as LiveData
    fun getWeatherData(): LiveData<List<WeatherDataRealmModel>> {
        val weatherDataLiveData = MutableLiveData<List<WeatherDataRealmModel>>()
        realm.where(WeatherDataRealmModel::class.java)
            .findAllAsync() // Perform asynchronous query to get all weather data
            .addChangeListener { results ->
                // Post the results to LiveData after copying them from Realm to avoid thread issues
                weatherDataLiveData.postValue(realm.copyFromRealm(results))
            }
        return weatherDataLiveData
    }

    // Function to retrieve hourly weather data stored in Realm and return it as LiveData
    fun getHourlyWeatherData(): LiveData<List<HourlyWeatherRealmModel>> {
        val hourlyDataLiveData = MutableLiveData<List<HourlyWeatherRealmModel>>()
        realm.where(HourlyWeatherRealmModel::class.java)
            .findAllAsync() // Perform asynchronous query to get all hourly weather data
            .addChangeListener { results ->
                // Post the results to LiveData after copying them from Realm to avoid thread issues
                hourlyDataLiveData.postValue(realm.copyFromRealm(results))
            }
        return hourlyDataLiveData
    }
}
