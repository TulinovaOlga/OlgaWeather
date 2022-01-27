package com.tulinova.olgaweather

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.tulinova.olgaweather.api.CustomError
import com.tulinova.olgaweather.api.Status
import com.tulinova.olgaweather.data.WeatherResponse
import com.tulinova.olgaweather.utils.IconsUtil
import com.tulinova.olgaweather.viewmodel.TodayWeatherViewModel
import kotlin.math.roundToInt
import androidx.appcompat.app.AppCompatActivity







class TodayWeatherFragment : Fragment() {

    private lateinit var humidityText: TextView
    private lateinit var rainLevelText: TextView
    private lateinit var pressureText: TextView
    private lateinit var windSpeedText: TextView
    private lateinit var windDirectionText: TextView
    private lateinit var locationText: TextView
    private lateinit var mainInfoText: TextView
    private lateinit var weatherImage: ImageView
    private lateinit var shareButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var mainInfoGroup: Group
    private lateinit var noInternetGroup: Group

    private val model: TodayWeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_today_weather, container, false)
        humidityText = rootView.findViewById(R.id.text_humidity)
        rainLevelText = rootView.findViewById(R.id.text_rain_mm)
        pressureText = rootView.findViewById(R.id.text_pressure)
        windSpeedText = rootView.findViewById(R.id.text_wind_speed)
        windDirectionText = rootView.findViewById(R.id.text_wind_direction)
        locationText = rootView.findViewById(R.id.text_location)
        mainInfoText = rootView.findViewById(R.id.text_main_info)
        weatherImage = rootView.findViewById(R.id.image_weather_today)
        progressBar = rootView.findViewById(R.id.progressBar)
        mainInfoGroup = rootView.findViewById(R.id.group)
        noInternetGroup = rootView.findViewById(R.id.group_no_internet)

        shareButton = rootView.findViewById(R.id.button_share)
        shareButton.setOnClickListener {

            val city = model.todayWeather.value?.data?.name ?: ""
            val temp = "${model.todayWeather.value?.data?.main?.temp?.roundToInt()} \u2103"
            val description = model.todayWeather.value?.data?.weather?.getOrNull(0)?.description ?: ""
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Today's temperature in $city is $temp. There is $description outside"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }

        (activity as AppCompatActivity?)?.title = getString(R.string.today)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //model.getTodayWeather(STUB_LATITUDE, STUB_LONGITUDE)
        model.todayWeather.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> showLoading()
                Status.SUCCESS -> showWeatherData(it.data)
                Status.ERROR -> showServerError(it.error)
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        mainInfoGroup.visibility = View.GONE
        noInternetGroup.visibility = View.GONE
    }

    private fun showWeatherData(data: WeatherResponse?) {
        progressBar.visibility = View.GONE
        mainInfoGroup.visibility = View.VISIBLE
        noInternetGroup.visibility = View.GONE

        locationText.text = (data?.name + ", " + data?.sys?.country)
        mainInfoText.text =
            ("" + data?.main?.temp?.roundToInt() + "℃ | " + data?.weather?.getOrNull(0)?.main)
        humidityText.text = ("" + data?.main?.humidity + "%")
        if(data?.rain?.rainIn3h != null) {
            rainLevelText.text = ("${data.rain.rainIn3h} mm")
        } else if(data?.rain?.rainIn1h != null) {
            rainLevelText.text = ("${data.rain.rainIn1h} mm")
        } else if (data?.snow?.snowIn1h != null) {
            rainLevelText.text = ("" + data.snow.snowIn1h + "mm")
        } else if(data?.snow?.snowIn3h != null) {
            rainLevelText.text = ("" + data.snow.snowIn3h + "mm")
        }  else {
            rainLevelText.text = ("0 mm")
        }
        pressureText.text = ("" + data?.main?.pressure + "hPa")
        windSpeedText.text = ("" + data?.wind?.speed + "m/s")
        windDirectionText.text = (degToCompass(data?.wind?.deg ?: 0))
        weatherImage.setImageResource(IconsUtil.getIconResId(data?.weather?.getOrNull(0)?.icon))


    }

    //TODO: replace this method outside of Fragment
    private fun degToCompass(num: Int): String {
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

    private fun showServerError(error: CustomError?) {
        progressBar.visibility = View.GONE
        mainInfoGroup.visibility = View.GONE
        noInternetGroup.visibility = View.VISIBLE

    }
}