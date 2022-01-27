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
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import androidx.fragment.app.DialogFragment

import com.tulinova.olgaweather.viewmodel.TodayWeatherViewModel
import android.content.Intent

import android.content.BroadcastReceiver
import android.content.IntentFilter import android.widget.Toolbar


const val LOG_TAG = "TulinApp"
const val STUB_LATITUDE = 51.50
const val STUB_LONGITUDE = -0.11

private const val PERMISSION_ACCESS_COARSE_LOCATION_CODE = 1010

class MainActivity : AppCompatActivity(),
    LocationRationaleDialogFragment.LocationRationaleDialogListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val model: TodayWeatherViewModel by viewModels()


    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    private val mGpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //showSnackbar(R.string.updateGPSstatus)
                //    Toast.makeText()
                getLastLocation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //val textTitle: TextView = findViewById(R.id.title)
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (hasLocationPermission()) {
            getLastLocation()
        } else {
            requestLocationPermission();
        }


        registerReceiver(mGpsSwitchStateReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                model.connectionLost()
                Log.d(LOG_TAG, "connectionLost")
            }

            override fun onUnavailable() {
                model.connectionLost()
                Log.d(LOG_TAG, "connectionLost")
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                model.connectionLost()
                Log.d(LOG_TAG, "connectionLost")
            }

            override fun onAvailable(network: Network) {
                model.connectionResumed()
                Log.d(LOG_TAG, "connectionResumed")

            }
        }

        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        ActivityCompat.requestPermissions(
            this, arrayOf(ACCESS_COARSE_LOCATION), PERMISSION_ACCESS_COARSE_LOCATION_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location: Location = task.result
                model.getTodayWeather(location.latitude, location.longitude)
                Toast.makeText(
                    this,
                    "Location is ${location.latitude} and ${location.longitude} ",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                showSnackbar(R.string.turn_on_gps)
                model.getTodayWeather(STUB_LATITUDE, STUB_LONGITUDE)
                //mGpsSwitchStateReceiver =

            }
        }

    }

    override fun onDestroy() {

        try {
            unregisterReceiver(mGpsSwitchStateReceiver)
        } catch(e : IllegalArgumentException ) {
            e.printStackTrace();
        }
        super.onDestroy()

    }

    private fun showSnackbar(
        snackStrId: Int,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content), getString(snackStrId),
            LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.anchorView = bottomNavigationView
        snackbar.show()
    }




    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
            val dialog = LocationRationaleDialogFragment()
            dialog.show(supportFragmentManager, "LocationRationaleDialogFragment")
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(ACCESS_COARSE_LOCATION), PERMISSION_ACCESS_COARSE_LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ACCESS_COARSE_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                showSnackbar(R.string.no_location_permission)
            }
        }
    }
}