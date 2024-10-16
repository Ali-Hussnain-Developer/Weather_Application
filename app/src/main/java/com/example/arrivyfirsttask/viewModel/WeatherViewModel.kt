package com.example.arrivyfirsttask.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arrivyfirsttask.model.local.HourlyWeatherRealmModel
import com.example.arrivyfirsttask.model.local.WeatherDataRealmModel
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem
import com.example.arrivyfirsttask.model.remote.HourlyWeatherResponse
import com.example.arrivyfirsttask.model.remote.WeatherResponse
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import com.example.arrivyfirsttask.sealed.ApiResult
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableStateFlow<ApiResult<WeatherResponse>?>(null)
    val weatherData: StateFlow<ApiResult<WeatherResponse>?> = _weatherData

    private val _hourlyWeatherData = MutableStateFlow<ApiResult<HourlyWeatherResponse>?>(null)
    val hourlyWeatherData: StateFlow<ApiResult<HourlyWeatherResponse>?> = _hourlyWeatherData

    val weatherDataRealm: LiveData<List<WeatherDataRealmModel>> = repository.getWeatherData()
    val hourlyWeatherDataRealm: LiveData<List<HourlyWeatherRealmModel>> = repository.getHourlyWeatherData()

    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch {
            repository.fetchWeatherByCity(city).collect { result ->
                _weatherData.value = result
            }
        }
    }

    fun fetchHourlyWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.fetchHourlyWeatherData(latitude, longitude).collect { result ->
                _hourlyWeatherData.value = result
            }
        }
    }



    fun saveWeatherData(weatherData: WeatherDataRealmModel) {
        // Perform Realm operations on IO thread
        viewModelScope.launch(Dispatchers.IO) {
            val realmDb = Realm.getDefaultInstance() // Create Realm instance within the IO thread
            try {
                realmDb.executeTransaction { realm ->
                    // Perform write operation
                    realm.copyToRealmOrUpdate(weatherData)
                }
            } catch (e: Exception) {
                // Handle any exceptions
            } finally {
                // Close Realm instance on the same thread it was created
                if (!realmDb.isClosed) {
                    realmDb.close()
                }
            }
        }
    }

    // Function to save list of hourly weather data

    fun saveHourlyWeatherData(hourlyData: List<HourlyWeatherItem>) {
        // Perform Realm operations on IO thread
        viewModelScope.launch(Dispatchers.IO) {
            val realmDb = Realm.getDefaultInstance()
            try {
                realmDb.executeTransaction { realm ->
                    hourlyData.forEachIndexed { index, item ->
                        val weatherDetail = HourlyWeatherRealmModel().apply {
                            id = index
                            time = item.time
                            temperature = item.temperature
                            weatherStatusIcon = item.icon
                        }
                        realm.copyToRealmOrUpdate(weatherDetail)
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions
            } finally {
                // Close Realm instance on the same thread it was created
                if (!realmDb.isClosed) {
                    realmDb.close()
                }
            }
        }
    }

}

