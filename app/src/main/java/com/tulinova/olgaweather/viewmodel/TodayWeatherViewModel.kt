package com.tulinova.olgaweather.viewmodel

import androidx.lifecycle.MutableLiveData
import com.tulinova.olgaweather.STUB_LATITUDE
import com.tulinova.olgaweather.STUB_LONGITUDE
import com.tulinova.olgaweather.api.CustomError
import com.tulinova.olgaweather.api.Event
import com.tulinova.olgaweather.data.WeatherResponse




class TodayWeatherViewModel : BaseViewModel() {

    val todayWeather = MutableLiveData<Event<WeatherResponse>>()


    fun connectionResumed(){
        if(todayWeather.hasActiveObservers()){
            getTodayWeather(STUB_LATITUDE, STUB_LONGITUDE)
        }

    }

    fun connectionLost(){
        if(todayWeather.hasActiveObservers()){
            todayWeather.postValue(Event.error(CustomError(101, "connection lost")))
        }
    }

    fun getTodayWeather(latitude: Double, longitude: Double) {
        requestWithLiveData(todayWeather) {
            api.getCurrentWeather(latitude, longitude)
        }
    }

}



