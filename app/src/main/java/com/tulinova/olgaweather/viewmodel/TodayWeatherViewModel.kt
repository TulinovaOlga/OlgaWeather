package com.tulinova.olgaweather.viewmodel

import androidx.lifecycle.MutableLiveData
import com.tulinova.olgaweather.data.Event
import com.tulinova.olgaweather.data.WeatherResponse




class TodayWeatherViewModel : BaseViewModel() {

    val todayWeather = MutableLiveData<Event<WeatherResponse>>()


    fun getTodayWeather(latitude: Double, longitude: Double) {
        requestWithLiveData(todayWeather) {
            api.getCurrentWeather(latitude, longitude)
        }
    }

}



