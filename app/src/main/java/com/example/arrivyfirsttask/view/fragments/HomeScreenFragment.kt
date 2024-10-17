package com.example.arrivyfirsttask.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.adapter.HourlyWeatherAdapter
import com.example.arrivyfirsttask.databinding.FragmentHomeScreenBinding
import com.example.arrivyfirsttask.model.local.WeatherDataRealmModel
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem
import com.example.arrivyfirsttask.model.remote.WeatherApiClient
import com.example.arrivyfirsttask.model.remote.WeatherResponse
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import com.example.arrivyfirsttask.sealed.ApiResult
import com.example.arrivyfirsttask.utils.DateUtils
import com.example.arrivyfirsttask.utils.DialogUtils
import com.example.arrivyfirsttask.utils.ImageUtils
import com.example.arrivyfirsttask.utils.KeyBoardUtils.hideKeyboard
import com.example.arrivyfirsttask.utils.ListUtils
import com.example.arrivyfirsttask.utils.NetworkUtil
import com.example.arrivyfirsttask.utils.TimeUtils
import com.example.arrivyfirsttask.viewModel.WeatherViewModel
import kotlinx.coroutines.launch

class HomeScreenFragment : Fragment() {
    // Binding variable for the layout
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init() // Initialize necessary components
    }

    private fun init() {
        // Initialize ViewModel and set up observers and listeners
        weatherViewModel = WeatherViewModel(WeatherRepository(WeatherApiClient.instance))
        setUpObserver() // Set up LiveData observers
        setUpListeners() // Set up click listeners for UI elements
        setTodayDate() // Display today's date
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver() {
        // Observe weather data from the ViewModel
        weatherViewModel.weatherDataRealm.observe(viewLifecycleOwner, Observer { weatherData ->
            if(weatherData != null && weatherData.isNotEmpty()) {
                weatherData.let {
                    // Update UI with weather data
                    binding.tvCityName.text = it[0].name
                    binding.tvTemperature.text = "${it[0].temperature}°C"
                    binding.tvMaxTemp.text = "Max: ${it[0].maxTemperature}°C "
                    binding.tvMinTemp.text = "Min: ${it[0].minTemperature}°C"
                    ImageUtils.loadWeatherIcon(
                        requireContext(),
                        it[0].weatherStatusIcon,
                        binding.ivWeatherStatusIcon
                    )
                }
            }
        })

        // Observe hourly weather data
        weatherViewModel.hourlyWeatherDataRealm.observe(viewLifecycleOwner, Observer { hourlyData ->
            val hourlyWeatherItems = ListUtils.convertToHourlyWeatherItem(hourlyData)
            setDataInRecyclerView(hourlyWeatherItems) // Update RecyclerView with hourly data
        })
    }

    private fun setTodayDate() {
        // Set today's date in the UI
        binding.tvTodayDate.text = DateUtils.getCurrentDate()
    }

    private fun setUpListeners() {
        // Set up click listener for the search button
        binding.btnSearchCity.setOnClickListener {
            val city = binding.EdtCityName.text.toString().trim()
            if (city.isNotEmpty()) {
                if (NetworkUtil.isInternetAvailable(requireContext())) {
                    weatherViewModel.fetchWeatherByCity(city) // Fetch weather data for the specified city
                    activity?.hideKeyboard() // Hide the keyboard
                    handleWeatherAPIResponse() // Handle API response
                } else {
                    // Show error message if no internet connection
                    Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show error message if city name is empty
                Toast.makeText(requireContext(), getString(R.string.enter_city_name), Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to the detail screen on click
        binding.layoutHourlyWeather.setOnClickListener {
            moveToWeatherDetailScreen()
        }
    }

    private fun moveToWeatherDetailScreen() {
        // Navigate to the detail screen
        findNavController().navigate(R.id.action_homeScreenFragment_to_detailScreenFragment)
    }

    private fun handleWeatherAPIResponse() {
        // Observe weather API responses in a coroutine
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE // Show progress bar while loading
                    }

                    is ApiResult.Success -> {
                        binding.progressBar.visibility = View.GONE // Hide progress bar on success
                        setWeatherData(result.data) // Update UI with weather data
                        saveWeatherDataToRealm(
                            result.data.name,
                            result.data.main.temp,
                            result.data.main.temp_max,
                            result.data.main.temp_min,
                            result.data.weather[0].icon,
                            result.data.sys.sunrise,
                            result.data.sys.sunset,
                            result.data.main.humidity,
                            result.data.wind.speed
                        ) // Save weather data to Realm database

                        // Fetch hourly weather data based on coordinates
                        weatherViewModel.fetchHourlyWeatherData(
                            result.data.coord.lat,
                            result.data.coord.lon
                        )

                        handleHourlyWeatherDataResponse() // Handle hourly weather data response
                    }

                    is ApiResult.Error -> {
                        binding.progressBar.visibility = View.GONE // Hide progress bar on error
                        DialogUtils.showErrorDialog(
                            requireContext(),
                            result.exception.message.toString() // Show error dialog
                        )
                    }

                    null -> {
                        binding.progressBar.visibility = View.GONE // Hide progress bar if null result
                    }
                }
            }
        }
    }

    private fun handleHourlyWeatherDataResponse() {
        // Collect hourly weather data in a separate coroutine
        lifecycleScope.launch {
            weatherViewModel.hourlyWeatherData.collect { hourlyResult ->
                when (hourlyResult) {
                    is ApiResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE // Show progress bar while loading
                    }

                    is ApiResult.Success -> {
                        binding.progressBar.visibility = View.GONE // Hide progress bar on success
                        // Update UI with hourly data
                        val hourlyItems = hourlyResult.data.list.map { hourlyData ->
                            val time = TimeUtils.convertTimestampToTime(hourlyData.dt) // Format timestamp to a readable time
                            val temperature = "${hourlyData.main.temp}°C" // Adjust temperature unit as needed
                            val icon = hourlyData.weather[0].icon // Get the icon code
                            HourlyWeatherItem(time, temperature, icon) // Create HourlyWeatherItem
                        }

                        setDataInRecyclerView(hourlyItems) // Update RecyclerView with hourly data
                        saveHourlyWeatherDataToRealm(hourlyItems) // Save hourly data to Realm
                    }

                    is ApiResult.Error -> {
                        binding.progressBar.visibility = View.GONE // Hide progress bar on error
                        // Handle error for hourly data
                        DialogUtils.showErrorDialog(
                            requireContext(),
                            hourlyResult.exception.message.toString() // Show error dialog
                        )
                    }

                    null -> {
                        binding.progressBar.visibility = View.GONE // Hide progress bar if null result
                    }
                }
            }
        }
    }

    private fun saveWeatherDataToRealm(
        cityName: String,
        temp: Double,
        maxTemp: Double,
        minTemp: Double,
        icon: String,
        sunrise: Long,
        sunset: Long,
        humidity: Int,
        speed: Double
    ) {
        // Create WeatherDataRealmModel and save it to Realm
        val weatherData = WeatherDataRealmModel().apply {
            id = 1
            name = cityName
            temperature = temp
            maxTemperature = maxTemp
            minTemperature = minTemp
            weatherStatusIcon = icon
            weatherHumidity = humidity
            windSpeed = speed
            sunRiseTime = sunrise
            sunSetTime = sunset
        }
        weatherViewModel.saveWeatherData(weatherData) // Save weather data to Realm
    }

    private fun saveHourlyWeatherDataToRealm(hourlyData: List<HourlyWeatherItem>) {
        // Save hourly weather data to Realm
        weatherViewModel.saveHourlyWeatherData(hourlyData)
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData(data: WeatherResponse) {
        // Update UI with the received weather data
        binding.tvTemperature.text = "${data.main.temp}°C"
        binding.tvCityName.text = data.name
        binding.tvMaxTemp.text = "Max: ${data.main.temp_max}°C "
        binding.tvMinTemp.text = "Min: ${data.main.temp_min}°C"
        ImageUtils.loadWeatherIcon(requireContext(), data.weather[0].icon, binding.ivWeatherStatusIcon)
    }

    private fun setDataInRecyclerView(hourlyWeatherItems: List<HourlyWeatherItem>) {
        // Set up RecyclerView for hourly weather data
        binding.rvTodayWeather.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTodayWeather.adapter = HourlyWeatherAdapter(hourlyWeatherItems) // Set adapter for RecyclerView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding when the view is destroyed
    }
}
