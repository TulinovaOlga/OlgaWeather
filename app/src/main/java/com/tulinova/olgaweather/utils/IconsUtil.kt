package com.tulinova.olgaweather.utils


import com.tulinova.olgaweather.R

object IconsUtil {

    fun getIconResId(serverId: String?): Int {
        return when (serverId) {
            "01d" -> R.drawable.ic_clear_sky_day
            "01n" -> R.drawable.ic_clear_sky_night
            "02d" -> R.drawable.ic_few_clouds_day
            "02n" -> R.drawable.ic_few_clouds_night
            "03d", "04d", "03n", "04n" -> R.drawable.ic_clouds
            "09d" -> R.drawable.ic_shower_rain_day
            "09n" -> R.drawable.ic_shower_rain
            "10d", "10n" -> R.drawable.ic_rain
            "11d", "11n" -> R.drawable.ic_thunder
            "13d", "13n" -> R.drawable.ic_snow
            "50d", "50n" -> R.drawable.ic_mist
            else -> R.drawable.ic_undefined_weather
        }
    }
}