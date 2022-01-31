package com.tulinova.olgaweather.utils

object WindUtils {

    fun degToCompass(num: Int): String {
        val index = (num / 22.5) + 0.5
        val directions: Array<String> = arrayOf(
            "N",
            "NNE",
            "NE",
            "ENE",
            "E",
            "ESE",
            "SE",
            "SSE",
            "S",
            "SSW",
            "SW",
            "WSW",
            "W",
            "WNW",
            "NW",
            "NNW",
            "N"
        )
        return directions[index.toInt()]
    }
}