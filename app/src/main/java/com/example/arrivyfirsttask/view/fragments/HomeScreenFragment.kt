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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.arrivyfirsttask.R
import com.example.arrivyfirsttask.RealmModel
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
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherViewModel: WeatherViewModel
  //  private var weatherRepository=WeatherRealmRepository()
    private var cityName:String?=null
    private var maxTemp:String?=null
    private var minTemp:String?=null
    private var sunRiseTime:String?=null
    private var sunSetTime:String?=null
    private var latitude:Double?=null
    private var longitude:Double?=null
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
        checkInternetConnection()
        setUpListeners()
        setTodayDate()
    }

    private fun setTodayDate() {
        binding.tvTodayDate.text = DateUtils.getCurrentDate()
    }

    private fun setUpListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            checkInternetConnection()
        }
        binding.btnSearchCity.setOnClickListener {
            val city = binding.EdtCityName.text.toString().trim()
            if (city.isNotEmpty()) {
                weatherViewModel.fetchWeatherByCity(city)
                activity?.hideKeyboard()
            }
            else{
                Toast.makeText(requireContext(),"Please Enter city Name",Toast.LENGTH_SHORT).show()
            }
        }
        binding.layoutHourlyWeather.setOnClickListener{
            senDataToWeatherDetailScreen()
        }

    }

    private fun senDataToWeatherDetailScreen() {
        val bundle = Bundle().apply {
            putString("cityName",cityName)
            putString("maxTemp",maxTemp)
            putString("minTemp",minTemp)
            putString("sunRiseTime",sunRiseTime)
            putString("sunSetTime",sunSetTime)
            latitude?.let { putDouble("latitude", it) }
            longitude?.let { putDouble("longitude", it) }
        }
        findNavController().navigate(R.id.action_homeScreenFragment_to_detailScreenFragment, bundle)
    }

    private fun checkInternetConnection() {
        if (NetworkUtil.isInternetAvailable(requireContext())) {
            callWeatherAPI()
        } else {
            callRealmData()
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun callRealmData() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = retrieveData()
            if (data.isNotEmpty()) {
                val realmModel = data[0]
                withContext(Dispatchers.Main) {
                    binding.tvCityName.text = realmModel.name
                    binding.tvTemperature.text = realmModel.temperature.toString()
                }
            }
            else{
                Log.d("fetchData", "No data found in Realm")
            }
            Log.d("fetchData", "Retrieved data: $data")
        }

    }
    private fun retrieveData(): List<RealmModel> {
        var realmDb = Realm.getDefaultInstance()
        val results: List<RealmModel> = realmDb.where(RealmModel::class.java).findAll()
        val dataList = realmDb.copyFromRealm(results)
        realmDb.close()
        return dataList
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
                       binding.progressBar.visibility=View.VISIBLE
                    }

                    is ApiResult.Success -> {
                        binding.progressBar.visibility=View.GONE
                        setWeatherData(result.data)

                        latitude = result.data.coord.lat
                        longitude = result.data.coord.lon
                        cityName=result.data.name
                        maxTemp=result.data.main.temp_max.toString()
                        minTemp=result.data.main.temp_min.toString()
                        sunRiseTime=result.data.sys.sunrise.toString()
                        sunSetTime=result.data.sys.sunset.toString()

                        saveDataToRealm(cityName!!,result.data.main.temp)

                        weatherViewModel.fetchHourlyWeatherData(latitude!!, longitude!!)

                        // Collect hourly weather data in a separate coroutine
                        launch {
                            weatherViewModel.hourlyWeatherData.collect { hourlyResult ->
                                when (hourlyResult) {
                                    is ApiResult.Loading -> {
                                        binding.progressBar.visibility=View.VISIBLE
                                    }

                                    is ApiResult.Success -> {
                                        binding.progressBar.visibility=View.GONE
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
                                        binding.progressBar.visibility=View.GONE
                                        // Handle error for hourly data
                                        Toast.makeText(requireContext(), "Error loading hourly data", Toast.LENGTH_SHORT).show()
                                        // Hide the refresh indicator in case of error
                                        binding.swipeRefreshLayout.isRefreshing = false
                                    }

                                    null -> {
                                        binding.progressBar.visibility=View.GONE
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
                        binding.progressBar.visibility=View.GONE
                        Toast.makeText(requireContext(), "Error loading weather data", Toast.LENGTH_SHORT).show()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    null -> {
                        binding.progressBar.visibility=View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    private  fun saveDataToRealm(cityName: String, temp: Double) {

        CoroutineScope(Dispatchers.IO).launch {
            var weatherDetail = RealmModel().apply {
                id=1
                name = cityName
                temperature = temp
            }

            var realmDb = Realm.getDefaultInstance() // get default Instance
            realmDb.beginTransaction()
            realmDb.copyToRealmOrUpdate(weatherDetail) // insert or update the data
            realmDb.commitTransaction()
            realmDb.close() // You need to close the database instance once the transaction done
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData(data: WeatherResponse) {
        binding.tvTemperature.text = "${data.main.temp}°C"
        binding.tvCityName.text = data.name
        binding.tvMaxTemp.text = "Max: ${data.main.temp_max}°C "
        binding.tvMinTemp.text = "Min: ${data.main.temp_min}°C"
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