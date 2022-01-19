package com.tulinova.olgaweather.api

import com.tulinova.olgaweather.data.WeatherResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



interface ApiInterface {
    @GET("forecast")
    fun getForecast(@Query("lat")latitude: Double, @Query("lon")longitude: Double, @Query("appid") apiKey:String)

    @GET("weather")
    fun getCurrentWeather(@Query("lat")latitude: Double, @Query("lon")longitude: Double, @Query("appid") apiKey:String) : Call<WeatherResponse>

    companion object {

        var BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun create() : ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }

}

