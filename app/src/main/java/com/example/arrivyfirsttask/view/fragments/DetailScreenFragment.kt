package com.example.arrivyfirsttask.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.arrivyfirsttask.databinding.FragmentDetailScreenBinding
import com.example.arrivyfirsttask.model.local.WeatherDataRealmModel
import com.example.arrivyfirsttask.utils.TimeUtils
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailScreenFragment : Fragment() {
    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!
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
        callRealmData()
    }

    @SuppressLint("SetTextI18n")
    private fun callRealmData() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = retrieveWeatherDataFromRealm()
            if (data.isNotEmpty()) {
                val realmModel = data[0]
                withContext(Dispatchers.Main) {
                    binding.tvCityName.text = realmModel.name
                    binding.tvMaxTemp.text = "Max: ${realmModel.maxTemperature}°C "
                    binding.tvMinTemp.text = "Min: ${realmModel.minTemperature}°C"
                    binding.tvHumidityValue.text = realmModel.weatherHumidity.toString()
                    binding.tvWindSpeedValue.text = realmModel.windSpeed.toString()
                    binding.tvSunSetTime.text =
                        "${TimeUtils.convertUnixToTime(realmModel.sunSetTime)} PM"
                    binding.tvSunRiseTime.text =
                        "${TimeUtils.convertUnixToTime(realmModel.sunRiseTime)} AM"


                }
            }
        }
    }

    private fun retrieveWeatherDataFromRealm(): List<WeatherDataRealmModel> {
        var realmDb = Realm.getDefaultInstance()
        val results: List<WeatherDataRealmModel> =
            realmDb.where(WeatherDataRealmModel::class.java).findAll()
        val dataList = realmDb.copyFromRealm(results)
        realmDb.close()
        return dataList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}