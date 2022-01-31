package com.tulinova.olgaweather.data

data class LocationModel(
    val availability: AVAILABILITY_STATE,
    var latitude: Double,
    var longitude: Double

)

enum class AVAILABILITY_STATE{
    NO_PERMISSION, NO_GPS, NULL_LOCATION, SUCCESS
}