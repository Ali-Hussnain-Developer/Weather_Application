package com.example.arrivyfirsttask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arrivyfirsttask.model.remote.HourlyWeatherItem
import com.example.arrivyfirsttask.utils.ImageUtils
import com.example.arrivyfirsttask.databinding.RvItemTodayWeatherBinding

class HourlyWeatherAdapter(private val items: List<HourlyWeatherItem>) :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    class HourlyWeatherViewHolder(private val binding: RvItemTodayWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourlyWeatherItem) {
            binding.tvTemperatureHourly.text = item.temperature
            binding.tvTimeHourly.text = item.time
            ImageUtils.loadWeatherIcon(
                binding.root.context,
                item.icon,
                binding.ivTemperatureIconHourly
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding =
            RvItemTodayWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
