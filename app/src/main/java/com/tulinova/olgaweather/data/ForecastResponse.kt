package com.tulinova.olgaweather.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class ForecastResponse(
    val list: List<ForecastEntry>,
    val city:City,
    val country:String
)

data class ForecastEntry(
    val main: Main,
    val weather: List<Weather>,
    @SerializedName("dt_txt")
    val date: Date,
    val dt : Long
)

data class City(
   val name:String
)




