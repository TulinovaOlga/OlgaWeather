package com.tulinova.olgaweather.viewmodel

import androidx.lifecycle.MutableLiveData
import com.tulinova.olgaweather.STUB_LATITUDE
import com.tulinova.olgaweather.STUB_LONGITUDE
import com.tulinova.olgaweather.api.CustomError
import com.tulinova.olgaweather.api.Event
import com.tulinova.olgaweather.data.ForecastResponse
import com.tulinova.olgaweather.data.WeatherResponse

class ForecastViewModel : BaseViewModel() {


    val forecastWeather = MutableLiveData<Event<ForecastResponse>>()


    fun connectionResumed() {
        if (forecastWeather.hasActiveObservers()) {
            getForecastWeather(STUB_LATITUDE, STUB_LONGITUDE)
        }

    }

    fun connectionLost() {
        if (forecastWeather.hasActiveObservers()) {
            forecastWeather.postValue(Event.error(CustomError(101, "connection lost")))
        }
    }

    fun getForecastWeather(latitude: Double, longitude: Double) {
        requestWithLiveData(forecastWeather) {
            api.getForecast(latitude, longitude, API_KEY, METRIC_UNITS)
        }
    }

}




