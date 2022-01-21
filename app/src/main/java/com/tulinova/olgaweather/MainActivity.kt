package com.tulinova.olgaweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import com.tulinova.olgaweather.adapters.ViewPagerAdapter
import com.tulinova.olgaweather.adapters.FORECAST_TAB_INDEX
import com.tulinova.olgaweather.adapters.TODAY_TAB_INDEX
import com.tulinova.olgaweather.viewmodel.TodayWeatherViewModel

const val LOG_TAG = "TulinApp"

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val model: TodayWeatherViewModel by viewModels()
    private var reqCode = 1010


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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        getLastLocation()


        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                model.connectionLost()
            }

            override fun onUnavailable() {
                model.connectionLost()
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                model.connectionLost()
            }

            override fun onAvailable(network: Network) {
                model.connectionResumed()

            }
        }

        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    }

    private fun getLastLocation() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    Toast.makeText(this, "Please Turn on GPS from settings", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Location is ${location.latitude} and ${location.longitude} ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            requestPermission()
        }
    }


    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun locationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        locationManager.allProviders
        val passiveProv = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
        return gpsEnabled || networkEnabled || passiveProv
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), reqCode
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == reqCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (locationEnable()) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(
                            this,
                            "Location is ${location.latitude} and ${location.longitude} ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please Turn on GPS from settings", Toast.LENGTH_SHORT).show()
            }
        }

    }
}