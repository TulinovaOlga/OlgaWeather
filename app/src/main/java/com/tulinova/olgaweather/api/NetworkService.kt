package com.tulinova.olgaweather.api

import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val API_KEY = "84b26273242b3de0fc7e008873f14fe5"
    private const val METRIC_UNITS = "metric"
    private const val API_KEY_PARAMETER = "appid"
    private const val UNITS_PARAMETER = "units"
    private const val SERVER_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    fun create(): ApiInterface {
        val gson = GsonBuilder().setDateFormat(SERVER_DATE_FORMAT).create()

        val requestInterceptor = Interceptor { chain ->

            val url = chain.request()
                .url
                .newBuilder()
                .addQueryParameter(API_KEY_PARAMETER, API_KEY)
                .addQueryParameter(UNITS_PARAMETER, METRIC_UNITS)
                .build()
            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiInterface::class.java)
    }
}

