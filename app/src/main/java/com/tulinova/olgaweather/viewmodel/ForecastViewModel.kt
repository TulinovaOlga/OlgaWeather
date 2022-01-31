package com.tulinova.olgaweather.viewmodel

import androidx.lifecycle.MutableLiveData

import com.tulinova.olgaweather.data.Event
import com.tulinova.olgaweather.data.ForecastResponse


class ForecastViewModel : BaseViewModel() {


    val forecastWeather = MutableLiveData<Event<ForecastResponse>>()



    fun getForecastWeather(latitude: Double, longitude: Double) {
        requestWithLiveData(forecastWeather) {
            api.getForecast(latitude, longitude)
        }
    }

}




