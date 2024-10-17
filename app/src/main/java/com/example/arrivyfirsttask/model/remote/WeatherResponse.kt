package com.example.arrivyfirsttask.model.remote

// Represents the response of the weather API for current weather
data class WeatherResponse(
    val coord: Coord,               // Coordinates of the location (latitude, longitude)
    val weather: List<Weather>,      // List of weather conditions (e.g., rain, clear)
    val base: String,                // Internal data, base station
    val main: Main,                  // Main weather data (temperature, humidity, pressure)
    val visibility: Int,             // Visibility in meters
    val wind: Wind,                  // Wind data (speed, direction)
    val clouds: Clouds,              // Cloud data (percentage of cloudiness)
    val dt: Long,                    // Time of data calculation (Unix, UTC)
    val sys: Sys,                    // System data (country, sunrise, sunset times)
    val timezone: Int,               // Shift in seconds from UTC
    val id: Int,                     // City ID
    val name: String,                // City name
    val cod: Int                     // HTTP response code (e.g., 200 for success)
)

// Represents the geographical coordinates of a location
data class Coord(
    val lon: Double,                 // Longitude
    val lat: Double                  // Latitude
)

// Represents the weather conditions (e.g., rain, clear)
data class Weather(
    val id: Int,                     // Weather condition ID
    val main: String,                // Group of weather parameters (Rain, Snow, etc.)
    val description: String,         // Weather condition within the group
    val icon: String                 // Icon representing the weather condition
)

// Represents the main weather details (e.g., temperature, pressure)
data class Main(
    val temp: Double,                // Current temperature
    val feels_like: Double,          // Perceived temperature
    val temp_min: Double,            // Minimum temperature
    val temp_max: Double,            // Maximum temperature
    val pressure: Int,               // Atmospheric pressure in hPa
    val humidity: Int,               // Humidity percentage
    val sea_level: Int,              // Atmospheric pressure at sea level
    val grnd_level: Int              // Atmospheric pressure at ground level
)

// Represents wind data (speed and direction)
data class Wind(
    val speed: Double,               // Wind speed in m/s
    val deg: Int                     // Wind direction in degrees
)

// Represents cloud data (percentage of cloudiness)
data class Clouds(
    val all: Int                     // Cloudiness percentage
)

// Represents system data (e.g., country, sunrise and sunset times)
data class Sys(
    val type: Int,                   // Internal parameter
    val id: Int,                     // Internal ID
    val country: String,             // Country code (e.g., US)
    val sunrise: Long,               // Sunrise time (Unix, UTC)
    val sunset: Long                 // Sunset time (Unix, UTC)
)

// Represents the response for hourly weather data
data class HourlyWeatherResponse(
    val list: List<HourlyData>,      // List of hourly weather data
    val city: City                   // City information
)

// Represents a single entry of hourly weather data
data class HourlyData(
    val dt: Long,                    // Time of data calculation (Unix, UTC)
    val main: Main,                  // Main weather details (temperature, humidity)
    val weather: List<Weather>,      // Weather conditions (list of weather details)
    val wind: Wind,                  // Wind data
    val clouds: Clouds,              // Cloud data
    val visibility: Int              // Visibility in meters
)

// Represents city information
data class City(
    val id: Int,                     // City ID
    val name: String,                // City name
    val coord: Coord,                // Coordinates of the city
    val country: String,             // Country code (e.g., US)
    val population: Int,             // Population of the city
    val timezone: Int,               // Shift in seconds from UTC
    val sunrise: Long,               // Sunrise time (Unix, UTC)
    val sunset: Long                 // Sunset time (Unix, UTC)
)

// Simplified representation of hourly weather data used for UI display
data class HourlyWeatherItem(
    val time: String,                // Time in a readable format (e.g., 12:00 PM)
    val temperature: String,         // Temperature (e.g., "23°C")
    val icon: String                 // Weather icon representing the condition
)
