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
import com.example.arrivyfirsttask.model.local.HourlyWeatherRealmModel
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.model.local.WeatherDataRealmModel
import com.example.arrivyfirsttask.adapter.HourlyWeatherAdapter
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem
import com.example.arrivyfirsttask.model.remote.WeatherResponse
import com.example.arrivyfirsttask.sealed.ApiResult
import com.example.arrivyfirsttask.utils.DateUtils
import com.example.arrivyfirsttask.utils.ImageUtils
import com.example.arrivyfirsttask.utils.KeyBoardUtils.hideKeyboard
import com.example.arrivyfirsttask.utils.NetworkUtil
import com.example.arrivyfirsttask.utils.TimeUtils
import com.example.arrivyfirsttask.databinding.FragmentHomeScreenBinding
import com.example.arrivyfirsttask.model.remote.WeatherApiClient
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import com.example.arrivyfirsttask.utils.DialogUtils
import com.example.arrivyfirsttask.utils.ListUtils
import com.example.arrivyfirsttask.viewModel.WeatherViewModel
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        weatherViewModel = WeatherViewModel(WeatherRepository(WeatherApiClient.instance))
        setUpObserver()
        setUpListeners()
        setTodayDate()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver() {
        weatherViewModel.weatherDataRealm.observe(viewLifecycleOwner, Observer { weatherData ->
            if(weatherData!=null && weatherData.isNotEmpty()){
            weatherData.let {
                binding.tvCityName.text = it[0].name
                binding.tvTemperature.text = "${it[0].temperature}°C"
                binding.tvMaxTemp.text = "Max: ${it[0].maxTemperature}°C "
                binding.tvMinTemp.text = "Min: ${it[0].minTemperature}°C"
                ImageUtils.loadWeatherIcon(
                    requireContext(),
                    it[0].weatherStatusIcon,
                    binding.ivWeatherStatusIcon
                )

            }}
            // Update your UI with weather data
        })
        weatherViewModel.hourlyWeatherDataRealm.observe(viewLifecycleOwner, Observer { hourlyData ->
            val hourlyWeatherItems = ListUtils.convertToHourlyWeatherItem(hourlyData)
            setDataInRecyclerView(hourlyWeatherItems)
        })

    }

    private fun setTodayDate() {
        binding.tvTodayDate.text = DateUtils.getCurrentDate()
    }

    private fun setUpListeners() {
        binding.btnSearchCity.setOnClickListener {
            val city = binding.EdtCityName.text.toString().trim()
            if (city.isNotEmpty()) {
                if (NetworkUtil.isInternetAvailable(requireContext())) {
                    weatherViewModel.fetchWeatherByCity(city)
                    activity?.hideKeyboard()
                    handleWeatherAPIResponse()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_city_name), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.layoutHourlyWeather.setOnClickListener {
            moveToWeatherDetailScreen()
        }

    }

    private fun moveToWeatherDetailScreen() {
        findNavController().navigate(R.id.action_homeScreenFragment_to_detailScreenFragment)
    }

    private fun handleWeatherAPIResponse() {
        // Observe weather data
        lifecycleScope.launch {
            weatherViewModel.weatherData.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setWeatherData(result.data)
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
                        )

                        weatherViewModel.fetchHourlyWeatherData(
                            result.data.coord.lat,
                            result.data.coord.lon
                        )

                        handleHourlyWeatherDataResponse()

                    }

                    is ApiResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        DialogUtils.showErrorDialog(
                            requireContext(),
                            result.exception.message.toString()
                        )

                    }

                    null -> {
                        binding.progressBar.visibility = View.GONE

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
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiResult.Success -> {
                        binding.progressBar.visibility = View.GONE
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

                        setDataInRecyclerView(hourlyItems)
                        saveHourlyWeatherDataToRealm(hourlyItems)
                    }

                    is ApiResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        // Handle error for hourly data
                        DialogUtils.showErrorDialog(
                            requireContext(),
                            hourlyResult.exception.message.toString()
                        )
                    }

                    null -> {
                        binding.progressBar.visibility = View.GONE
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
        weatherViewModel.saveWeatherData(weatherData)

    }

    private fun saveHourlyWeatherDataToRealm(hourlyData: List<HourlyWeatherItem>) {
        weatherViewModel.saveHourlyWeatherData(hourlyData)
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData(data: WeatherResponse) {
        binding.tvTemperature.text = "${data.main.temp}°C"
        binding.tvCityName.text = data.name
        binding.tvMaxTemp.text = "Max: ${data.main.temp_max}°C "
        binding.tvMinTemp.text = "Min: ${data.main.temp_min}°C"
        ImageUtils.loadWeatherIcon(
            requireContext(),
            data.weather[0].icon,
            binding.ivWeatherStatusIcon
        )
    }

    private fun setDataInRecyclerView(hourlyItems: List<HourlyWeatherItem>) {
        binding.rvTodayWeather.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val hourlyWeatherAdapter = HourlyWeatherAdapter(hourlyItems)
        binding.rvTodayWeather.adapter = hourlyWeatherAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding to null to avoid memory leaks
        _binding = null
    }

}

