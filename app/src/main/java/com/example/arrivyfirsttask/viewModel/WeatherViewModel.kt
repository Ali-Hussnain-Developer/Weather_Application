package com.example.arrivyfirsttask.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arrivyfirsttask.classes.data.WeatherResponse
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import com.example.arrivyfirsttask.classes.sealed.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<ApiResult<WeatherResponse>?>(null)
    val weatherData: StateFlow<ApiResult<WeatherResponse>?> = _weatherData

    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch {
            repository.fetchWeatherByCity(city).collect { result ->
                _weatherData.value = result
            }
        }
    }
}
