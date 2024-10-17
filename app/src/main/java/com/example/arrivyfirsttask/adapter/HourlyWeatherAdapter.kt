package com.example.arrivyfirsttask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem
import com.example.arrivyfirsttask.utils.ImageUtils
import com.example.arrivyfirsttask.databinding.RvItemTodayWeatherBinding

// Adapter for displaying a list of hourly weather data in a RecyclerView
class HourlyWeatherAdapter(private val weatherItems: List<HourlyWeatherItem>) :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    // ViewHolder class to hold and bind views for each item in the RecyclerView
    class HourlyWeatherViewHolder(private val binding: RvItemTodayWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Function to bind data from an HourlyWeatherItem to the UI elements in the ViewHolder
        fun bind(weatherItem: HourlyWeatherItem) {
            binding.tvTemperatureHourly.text = weatherItem.temperature
            binding.tvTimeHourly.text = weatherItem.time
            ImageUtils.loadWeatherIcon(
                binding.root.context,
                weatherItem.icon,
                binding.ivTemperatureIconHourly
            )
        }
    }

    // Called when RecyclerView needs a new ViewHolder for an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding = RvItemTodayWeatherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HourlyWeatherViewHolder(binding)
    }

    // Called to bind data to the ViewHolder at the specified position in the list
    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(weatherItems[position])
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = weatherItems.size
}
