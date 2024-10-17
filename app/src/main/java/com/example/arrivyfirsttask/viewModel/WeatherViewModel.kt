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

    // StateFlow to handle weather data from the API
    private val _weatherData = MutableStateFlow<ApiResult<WeatherResponse>?>(null)
    val weatherData: StateFlow<ApiResult<WeatherResponse>?> = _weatherData

    // StateFlow to handle hourly weather data from the API
    private val _hourlyWeatherData = MutableStateFlow<ApiResult<HourlyWeatherResponse>?>(null)
    val hourlyWeatherData: StateFlow<ApiResult<HourlyWeatherResponse>?> = _hourlyWeatherData

    // LiveData to retrieve weather data stored in Realm
    val weatherDataRealm: LiveData<List<WeatherDataRealmModel>> = repository.getWeatherData()

    // LiveData to retrieve hourly weather data stored in Realm
    val hourlyWeatherDataRealm: LiveData<List<HourlyWeatherRealmModel>> = repository.getHourlyWeatherData()

    // Fetch weather data from the API based on city name
    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch {
            repository.fetchWeatherByCity(city).collect { result ->
                _weatherData.value = result
            }
        }
    }

    // Fetch hourly weather data from the API based on coordinates (latitude, longitude)
    fun fetchHourlyWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.fetchHourlyWeatherData(latitude, longitude).collect { result ->
                _hourlyWeatherData.value = result
            }
        }
    }

    // Function to save weather data (single object) into Realm
    fun saveWeatherData(weatherData: WeatherDataRealmModel) {
        // Perform Realm operations on the IO thread for background execution
        viewModelScope.launch(Dispatchers.IO) {
            val realmDb = Realm.getDefaultInstance() // Create Realm instance in the IO thread
            try {
                // Execute the transaction to save data in Realm
                realmDb.executeTransaction { realm ->
                    realm.copyToRealmOrUpdate(weatherData)
                }
            } catch (e: Exception) {
                // Handle any potential exceptions (e.g., Realm errors)
                e.printStackTrace()
            } finally {
                // Ensure the Realm instance is closed on the same thread it was opened
                if (!realmDb.isClosed) {
                    realmDb.close()
                }
            }
        }
    }

    // Function to save a list of hourly weather data into Realm
    fun saveHourlyWeatherData(hourlyData: List<HourlyWeatherItem>) {
        // Perform Realm operations on the IO thread for background execution
        viewModelScope.launch(Dispatchers.IO) {
            val realmDb = Realm.getDefaultInstance() // Create Realm instance
            try {
                // Execute the transaction to save list data in Realm
                realmDb.executeTransaction { realm ->
                    hourlyData.forEachIndexed { index, item ->
                        val hourlyWeather = HourlyWeatherRealmModel().apply {
                            id = index
                            time = item.time
                            temperature = item.temperature
                            weatherStatusIcon = item.icon
                        }
                        realm.copyToRealmOrUpdate(hourlyWeather)
                    }
                }
            } catch (e: Exception) {
                // Handle any potential exceptions
                e.printStackTrace()
            } finally {
                // Close Realm instance once the transaction is done
                if (!realmDb.isClosed) {
                    realmDb.close()
                }
            }
        }
    }
}

