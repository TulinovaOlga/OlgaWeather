package com.tulinova.olgaweather

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tulinova.olgaweather.adapters.ViewPagerAdapter
import com.tulinova.olgaweather.api.ApiInterface
import com.tulinova.olgaweather.data.WeatherResponse
import com.tulinova.olgaweather.adapters.FORECAST_TAB_INDEX
import com.tulinova.olgaweather.adapters.TODAY_TAB_INDEX
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val STUB_LATITUDE = 53.89
const val STUB_LONGITUDE = 27.56
private const val API_KEY = "84b26273242b3de0fc7e008873f14fe5"
const val LOG_TAG = "TulinApp"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val textTitle: TextView = findViewById(R.id.title)

        val viewPagerAdapter = ViewPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapter
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                TODAY_TAB_INDEX -> tab.text = getString(R.string.tab_today_title)
                FORECAST_TAB_INDEX -> tab.text = getString(R.string.tab_forecast_title)
            }
        }.attach()

        val apiInterface =
            ApiInterface.create().getCurrentWeather(STUB_LATITUDE, STUB_LONGITUDE, API_KEY)

        apiInterface.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>?,
                response: Response<WeatherResponse>?
            ) {
                if (response?.body() != null) {
                    textTitle.text = response.body()?.name ?: ""
                    Log.d(LOG_TAG, response.body().toString())
                }
            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.network_error) + t?.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        })


    }
}