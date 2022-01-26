package com.tulinova.olgaweather.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkService {
    private var BASE_URL = "https://api.openweathermap.org/data/2.5/"

    fun create(): ApiInterface {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
          //  .addQueryParameter("key", API_KEY)
            .build()
            .create(ApiInterface::class.java)
    }
}

