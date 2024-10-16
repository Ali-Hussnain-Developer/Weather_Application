package com.example.arrivyfirsttask.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arrivyfirsttask.classes.data.HourlyWeatherResponse
import com.example.arrivyfirsttask.classes.data.WeatherResponse
import com.example.arrivyfirsttask.classes.sealed.ApiResult
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherData = MutableStateFlow<ApiResult<WeatherResponse>?>(null)
    val weatherData: StateFlow<ApiResult<WeatherResponse>?> = _weatherData

    private val _hourlyWeatherData = MutableStateFlow<ApiResult<HourlyWeatherResponse>?>(null)
    val hourlyWeatherData: StateFlow<ApiResult<HourlyWeatherResponse>?> = _hourlyWeatherData

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
}

