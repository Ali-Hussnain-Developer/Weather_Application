package com.example.arrivyfirsttask.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.arrivyfirsttask.adapter.HourlyWeatherAdapter
import com.example.arrivyfirsttask.classes.data.HourlyWeatherItem
import com.example.arrivyfirsttask.classes.data.WeatherResponse
import com.example.arrivyfirsttask.classes.sealed.ApiResult
import com.example.arrivyfirsttask.classes.utils.DateUtils
import com.example.arrivyfirsttask.classes.utils.KeyBoardUtils.hideKeyboard
import com.example.arrivyfirsttask.classes.utils.NetworkUtil
import com.example.arrivyfirsttask.classes.utils.TimeUtils
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
        binding.btnSearchCity.setOnClickListener {
            val city = binding.EdtCityName.text.toString()
            if (city.isNotEmpty()) {
                weatherViewModel.fetchWeatherByCity(city)
                activity?.hideKeyboard()
            }
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
        weatherViewModel = WeatherViewModel(WeatherRepository(WeatherApiClient.instance))

        // Show the loading indicator when the API call starts
        binding.swipeRefreshLayout.isRefreshing = true

        // Observe weather data
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        // API call is in progress; you might show a loading spinner if needed
                    }

                    is ApiResult.Success -> {
                        setWeatherData(result.data)

                        // Call for hourly weather data using the obtained latitude and longitude
                        val latitude = result.data.coord.lat
                        val longitude = result.data.coord.lon
                        weatherViewModel.fetchHourlyWeatherData(latitude, longitude)

                        // Collect hourly weather data in a separate coroutine
                        launch {
                            weatherViewModel.hourlyWeatherData.collect { hourlyResult ->
                                when (hourlyResult) {
                                    is ApiResult.Loading -> {
                                        // You can show a loading spinner for hourly data if needed
                                    }

                                    is ApiResult.Success -> {
                                        Log.d("Hourly Weather Result", "${hourlyResult.data}")
                                        // Update UI with hourly data
                                        val hourlyItems = hourlyResult.data.list.map { hourlyData ->
                                            val time =
                                                TimeUtils.convertTimestampToTime(hourlyData.dt) // Format timestamp to a readable time
                                            val temperature =
                                                "${hourlyData.main.temp}°C" // Adjust temperature unit as needed
                                            val icon =
                                                hourlyData.weather[0].icon // Get the icon code
                                            HourlyWeatherItem(time, temperature, icon)
                                        }

                                        // Setup RecyclerView with the hourly weather data
                                        binding.rvTodayWeather.layoutManager = LinearLayoutManager(
                                            context,
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )

                                        val hourlyWeatherAdapter = HourlyWeatherAdapter(hourlyItems)
                                        binding.rvTodayWeather.adapter = hourlyWeatherAdapter

                                        // Hide the refresh indicator once data is loaded
                                        binding.swipeRefreshLayout.isRefreshing = false
                                    }

                                    is ApiResult.Error -> {
                                        // Handle error for hourly data
                                        Toast.makeText(requireContext(), "Error loading hourly data", Toast.LENGTH_SHORT).show()
                                        // Hide the refresh indicator in case of error
                                        binding.swipeRefreshLayout.isRefreshing = false
                                    }

                                    null -> {
                                        // Handle null case for hourly data
                                        binding.swipeRefreshLayout.isRefreshing = false
                                    }
                                }
                            }
                        }
                        // Hide the refresh indicator once weather data is loaded
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    is ApiResult.Error -> {
                        // Handle error for weather data
                        Toast.makeText(requireContext(), "Error loading weather data", Toast.LENGTH_SHORT).show()
                        // Hide the refresh indicator in case of error
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    null -> {
                        // Handle null case for weather data
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }



    @SuppressLint("SetTextI18n")
    private fun setWeatherData(data: WeatherResponse) {
        binding.tvTemperature.text = "${data.main.temp}°C"
        binding.tvCityName.text = data.name
        binding.tvCityName.text = data.name
        binding.tvMaxTemp.text = "Max: ${data.main.temp_max}°C "
        binding.tvMinTemp.text = "Min: ${data.main.temp_min}°C"
        binding.tvTodayDate.text = DateUtils.getCurrentDate()
        loadWeatherIcon(data.weather[0].icon)
    }

    private fun loadWeatherIcon(icon: String) {
        // Construct the URL for the weather icon
        val iconUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(binding.ivWeatherStatusIcon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }
}