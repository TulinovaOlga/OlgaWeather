package com.tulinova.olgaweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.tulinova.olgaweather.adapters.ForecastListAdapter
import com.tulinova.olgaweather.api.CustomError
import com.tulinova.olgaweather.api.Status
import com.tulinova.olgaweather.data.ForecastEntry
import com.tulinova.olgaweather.data.ForecastResponse
import com.tulinova.olgaweather.data.WeatherResponse
import com.tulinova.olgaweather.utils.IconsUtil
import com.tulinova.olgaweather.viewmodel.ForecastViewModel
import com.tulinova.olgaweather.viewmodel.TodayWeatherViewModel
import kotlin.math.roundToInt


class ForecastFragment : Fragment() {

    private val model: ForecastViewModel by activityViewModels()
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_forecast, container, false)
        recyclerView = view.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getForecastWeather(STUB_LATITUDE, STUB_LONGITUDE)
        model.forecastWeather.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> showLoading()
                Status.SUCCESS -> showWeatherData(it.data)
                Status.ERROR -> showServerError(it.error)
            }
        })
    }


    private fun showLoading() {

//        progressBar.visibility = View.VISIBLE
//        mainInfoGroup.visibility = View.GONE
//        noInternetGroup.visibility = View.GONE
    }

    private fun showWeatherData(forecastData: ForecastResponse?) {
//        progressBar.visibility = View.GONE
//        mainInfoGroup.visibility = View.VISIBLE
//        noInternetGroup.visibility = View.GONE
//
        if (forecastData != null) {
            recyclerView.adapter = ForecastListAdapter(forecastData?.list)
            // ForecastListAdapter(forecastData?.list)

            recyclerView.invalidate();
        }

    }

    private fun showServerError(error: CustomError?) {
        Toast.makeText(context, error?.errorMes, Toast.LENGTH_LONG).show();
    }


}