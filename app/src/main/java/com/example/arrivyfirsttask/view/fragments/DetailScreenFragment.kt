package com.example.arrivyfirsttask.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.arrivyfirsttask.databinding.FragmentDetailScreenBinding
import com.example.arrivyfirsttask.model.remote.WeatherApiClient
import com.example.arrivyfirsttask.model.repository.WeatherRepository
import com.example.arrivyfirsttask.utils.TimeUtils
import com.example.arrivyfirsttask.viewModel.WeatherViewModel


class DetailScreenFragment : Fragment() {
    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding and return the root view
        _binding = FragmentDetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init() // Initialize the fragment
    }


    private fun init() {
        setUpObserver() // Set up observer for weather data
    }

    @SuppressLint("SetTextI18n") // Suppress lint warning for setting text with concatenation
    private fun setUpObserver() {
        // Initialize the ViewModel with a WeatherRepository instance
        weatherViewModel = WeatherViewModel(WeatherRepository(WeatherApiClient.instance))

        // Observe weather data from the ViewModel
        weatherViewModel.weatherDataRealm.observe(viewLifecycleOwner) { weatherData ->
            if (weatherData != null && weatherData.isNotEmpty()) {
                weatherData.let {
                    // Update UI components with weather data
                    binding.tvCityName.text = it[0].name
                    binding.tvMaxTemp.text = "Max: ${it[0].maxTemperature}°C "
                    binding.tvMinTemp.text = "Min: ${it[0].minTemperature}°C"
                    binding.tvHumidityValue.text = it[0].weatherHumidity.toString()
                    binding.tvWindSpeedValue.text = it[0].windSpeed.toString()
                    binding.tvSunSetTime.text =
                        "${TimeUtils.convertUnixToTime(it[0].sunSetTime)} PM"
                    binding.tvSunRiseTime.text =
                        "${TimeUtils.convertUnixToTime(it[0].sunRiseTime)} AM"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Set binding to null to avoid memory leaks
    }
}