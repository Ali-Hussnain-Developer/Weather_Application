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
    private val realm: Realm = Realm.getDefaultInstance()

     fun fetchWeatherByCity(city: String): Flow<ApiResult<WeatherResponse>> = flow {
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

    fun fetchHourlyWeatherData(latitude: Double, longitude: Double): Flow<ApiResult<HourlyWeatherResponse>> = flow {
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

    // Function to retrieve weather data as LiveData
    fun getWeatherData(): LiveData<List<WeatherDataRealmModel>> {
        val weatherDataLiveData = MutableLiveData<List<WeatherDataRealmModel>>()
        realm.where(WeatherDataRealmModel::class.java)
            .findAllAsync()
            .addChangeListener { results ->
                weatherDataLiveData.postValue(realm.copyFromRealm(results))
            }
        return weatherDataLiveData
    }

    // Function to retrieve hourly weather data as LiveData
    fun getHourlyWeatherData(): LiveData<List<HourlyWeatherRealmModel>> {
        val hourlyDataLiveData = MutableLiveData<List<HourlyWeatherRealmModel>>()
        realm.where(HourlyWeatherRealmModel::class.java)
            .findAllAsync()
            .addChangeListener { results ->
                hourlyDataLiveData.postValue(realm.copyFromRealm(results))
            }
        return hourlyDataLiveData
    }

}
