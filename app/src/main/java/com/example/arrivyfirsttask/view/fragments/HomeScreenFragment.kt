package com.example.arrivyfirsttask.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.arrivyfirsttask.classes.data.WeatherResponse
import com.example.arrivyfirsttask.classes.sealed.ApiResult
import com.example.arrivyfirsttask.classes.utils.DateUtils
import com.example.arrivyfirsttask.classes.utils.KeyBoardUtils
import com.example.arrivyfirsttask.classes.utils.KeyBoardUtils.hideKeyboard
import com.example.arrivyfirsttask.classes.utils.NetworkUtil
import com.example.arrivyfirsttask.databinding.FragmentHomeScreenBinding
import com.example.arrivyfirsttask.model.api.WeatherApiClient
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import com.example.arrivyfirsttask.viewModel.WeatherViewModel
import kotlinx.coroutines.launch

class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherViewModel: WeatherViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using the binding object
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        checkInternetConnection()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            checkInternetConnection()
        }

    }

    private fun checkInternetConnection() {
        if (NetworkUtil.isInternetAvailable(requireContext())) {
            // Internet is available, perform API call
              callWeatherAPI()
        } else {
            // Show a message or take appropriate action
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun callWeatherAPI() {



        // Initialize ViewModel
        weatherViewModel = WeatherViewModel(WeatherRepository(WeatherApiClient.instance))
        binding.swipeRefreshLayout.isRefreshing = true  // Show the loading indicator
        // Observe weather data
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {

                    }
                    is ApiResult.Success -> {
                        //resultTextView.text = formatWeatherData(result.data)
                        Log.d("result","${result.data}")
                        setDataToUI(result.data)
                    }
                    is ApiResult.Error -> {

                    }
                    null -> {

                    }
                }
            }

        }
        binding.swipeRefreshLayout.isRefreshing = false

       binding.btnSearchCity.setOnClickListener {
            val city = binding.EdtCityName.text.toString()
            if (city.isNotEmpty()) {
                weatherViewModel.fetchWeatherByCity(city)
                activity?.hideKeyboard()
            }
        }

    }
    @SuppressLint("SetTextI18n")
    private fun setDataToUI(data: WeatherResponse) {
        binding.tvTemperature.text = "${data.main.temp}°C"
        binding.tvCityName.text = data.name
        binding.tvCityName.text = data.name
        binding.tvMaxTemp.text = "Max: ${data.main.temp_max}°C "
        binding.tvMinTemp.text = "Min: ${data.main.temp_min}°C"
        binding.tvTodayDate.text=DateUtils.getCurrentDate()
        Log.d("DateToday", DateUtils.getCurrentDate())

        loadweatherIcon(data.weather[0].icon)
    }

    private fun loadweatherIcon(icon: String) {
        // Construct the URL for the weather icon
        val iconUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(binding.ivWeatherStatusIcon)
    }

    private fun formatWeatherData(weather: WeatherResponse?): String {
        return """
            City: ${weather?.name}
            Temperature: ${weather?.main?.temp}°C
            Condition: ${weather?.weather?.get(0)?.description}
            Humidity: ${weather?.main?.humidity}%
            Wind Speed: ${weather?.wind?.speed} m/s
            Sunrise: ${formatTime(weather?.sys?.sunrise)}
            Sunset: ${formatTime(weather?.sys?.sunset)}
        """.trimIndent()
    }

    private fun formatTime(timestamp: Long?): String {
        return timestamp?.let {
            val date = java.util.Date(it * 1000) // Convert seconds to milliseconds
            val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            sdf.format(date)
        } ?: "N/A"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }
}