package com.example.arrivyfirsttask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.arrivyfirsttask.classes.data.HourlyWeatherItem
import com.example.arrivyfirsttask.databinding.RvItemTodayWeatherBinding

class HourlyWeatherAdapter(private val items: List<HourlyWeatherItem>) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    class HourlyWeatherViewHolder(private val binding: RvItemTodayWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourlyWeatherItem) {
            binding.tvTemperatureHourly.text = item.temperature
            binding.tvTimeHourly.text = item.time
            // Load the icon using Glide
            val iconUrl = "https://openweathermap.org/img/wn/${item.icon}@2x.png"
            Glide.with(binding.root.context)
                .load(iconUrl)
                .into(binding.ivTemperatureIconHourly)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding = RvItemTodayWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
