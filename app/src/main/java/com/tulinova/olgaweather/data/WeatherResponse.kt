package com.tulinova.olgaweather.data

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val name: String,
    val rain: Rain,
    val snow: Snow
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Sys(
    val sunrise: Int,
    val sunset: Int,
    val country: String
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Rain(
    @SerializedName("3h")
    val rainIn3h: Double,
    @SerializedName("1h")
    val rainIn1h: Double
)

data class Snow(
    @SerializedName("3h")
    val snowIn3h: Double,
    @SerializedName("1h")
    val snowIn1h: Double
)




