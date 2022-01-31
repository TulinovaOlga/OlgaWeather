package com.tulinova.olgaweather


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tulinova.olgaweather.adapters.ForecastListAdapter
import com.tulinova.olgaweather.data.Status
import com.tulinova.olgaweather.data.ForecastResponse
import com.tulinova.olgaweather.viewmodel.ForecastViewModel
import com.tulinova.olgaweather.viewmodel.LocationViewModel
import okhttp3.ResponseBody


class ForecastFragment : Fragment() {
    private val model: ForecastViewModel by activityViewModels()
    private val locationModel: LocationViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var noInternetGroup:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_forecast, container, false)
        progressBar = view.findViewById(R.id.progress_bar)
        noInternetGroup = view.findViewById(R.id.group_no_internet)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = null

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

        swipeContainer = view.findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener {
            if (locationModel.locationData.value != null) {
                model.getForecastWeather(
                    locationModel.locationData.value!!.latitude,
                    locationModel.locationData.value!!.longitude
                )
                recyclerView.visibility = View.INVISIBLE
            }
        }
        swipeContainer.setColorSchemeResources(R.color.blue_700)
        (activity as AppCompatActivity?)?.title = getString(R.string.forecast)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationModel.locationData.observe(viewLifecycleOwner, {
            model.getForecastWeather(it.latitude, it.longitude)
        })

        model.forecastWeather.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> showLoading()
                Status.SUCCESS -> showWeatherData(it.data)
                Status.ERROR -> showServerError(it.error)
            }
        })
    }


    private fun showLoading() {
        if (!swipeContainer.isRefreshing) {
            progressBar.visibility = View.VISIBLE
            swipeContainer.visibility = View.GONE
        }
        noInternetGroup.visibility = View.GONE
    }

    private fun showWeatherData(forecastData: ForecastResponse?) {
        progressBar.visibility = View.GONE
        swipeContainer.visibility = View.VISIBLE
        swipeContainer.isRefreshing = false
        recyclerView.visibility = View.VISIBLE
        noInternetGroup.visibility = View.GONE
        if (forecastData != null) {
            (activity as AppCompatActivity?)?.title = forecastData.city.name
            recyclerView.adapter = ForecastListAdapter(forecastData.list)
            recyclerView.invalidate()
        }
    }

    private fun showServerError(error: ResponseBody?) {
        swipeContainer.isRefreshing = false
        noInternetGroup.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE

    }


}