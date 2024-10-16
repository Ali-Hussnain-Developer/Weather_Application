package com.example.arrivyfirsttask.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.arrivyfirsttask.utils.TimeUtils
import com.example.arrivyfirsttask.databinding.FragmentDetailScreenBinding

class DetailScreenFragment : Fragment() {
    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!
    private var cityName: String? = null
    private var maxTemp: String? = null
    private var minTemp: String? = null
    private var sunRiseTime: String? = null
    private var sunSetTime: String? = null
    private var humidity: String? = null
    private var windSpeed: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        receiveDataFromHomeScreen()
        setWeatherData()
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData() {
        binding.tvMaxTemp.text = "Max: $maxTemp °"
        binding.tvMinTemp.text = "Min: $minTemp °"
        binding.tvCityName.text = cityName
        binding.tvSunSetTime.text =
            "${sunSetTime?.let { TimeUtils.convertUnixToTime(it.toLong()) }} PM"
        binding.tvSunRiseTime.text =
            "${sunRiseTime?.let { TimeUtils.convertUnixToTime(it.toLong()) }} AM"
        binding.tvWindSpeedValue.text = windSpeed
        binding.tvHumidityValue.text = humidity
    }

    private fun receiveDataFromHomeScreen() {
        cityName = arguments?.getString("cityName")
        maxTemp = arguments?.getString("maxTemp")
        minTemp = arguments?.getString("minTemp")
        sunRiseTime = arguments?.getString("sunRiseTime")
        sunSetTime = arguments?.getString("sunSetTime")
        humidity = arguments?.getString("humidity")
        windSpeed = arguments?.getString("windSpeed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}