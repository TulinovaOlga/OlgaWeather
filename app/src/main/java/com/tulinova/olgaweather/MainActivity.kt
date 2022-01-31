package com.tulinova.olgaweather

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.*
import androidx.fragment.app.DialogFragment

import android.os.Looper
import com.google.android.gms.location.*
import com.tulinova.olgaweather.data.AVAILABILITY_STATE
import com.tulinova.olgaweather.viewmodel.LocationViewModel


private const val LOCATION_PERMISSION_REQUEST_CODE = 1010

class MainActivity : AppCompatActivity(),
    LocationRationaleDialogFragment.LocationRationaleDialogListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationModel: LocationViewModel by viewModels()


    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var containerLayout: View
    private var snackBar: Snackbar? = null
    private lateinit var locationCallback: LocationCallback

    private val mGpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!! == LocationManager.PROVIDERS_CHANGED_ACTION) {
                if (hasLocationPermission() && isLocationEnabled()) {
                    getLastLocationAndStartUpdates()
                    snackBar?.dismiss()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        containerLayout = findViewById(R.id.container_layout)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for (location in locationResult.locations) {

                    val newLocation =
                        locationModel.obtainLocation(AVAILABILITY_STATE.SUCCESS,
                            location.latitude,
                            location.longitude)
                    if (newLocation != null) {
                        locationModel.locationData.postValue(newLocation)
                    }
                    if (snackBar != null) {
                        snackBar?.dismiss()
                        snackBar = null
                    }
                }
            }
        }

        registerReceiver(
            mGpsSwitchStateReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

//        val networkCallback = object : ConnectivityManager.NetworkCallback() {
//            override fun onLost(network: Network) {
//                model.connectionLost()
//                Log.d(LOG_TAG, "connectionLost")
//            }
//
//            override fun onUnavailable() {
//                model.connectionLost()
//                Log.d(LOG_TAG, "connectionLost")
//            }
//
//            override fun onLosing(network: Network, maxMsToLive: Int) {
//                model.connectionLost()
//                Log.d(LOG_TAG, "connectionLost")
//            }
//
//            override fun onAvailable(network: Network) {
//                model.connectionResumed()
//                Log.d(LOG_TAG, "connectionResumed")
//
//            }
//        }

//        val connectivityManager =
//            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkRequest = NetworkRequest.Builder().build()
//        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    }

    override fun onStart() {
        super.onStart()
        if (hasLocationPermission()) {
            checkLocationAvailabilityAndGetLocation()
        } else {
            requestLocationPermission()
        }
    }


    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationAvailabilityAndGetLocation() {
        if (isLocationEnabled()) {
            getLastLocationAndStartUpdates()
        } else {
            turnGPSOn { IsGPSTurnedOn ->
                if (IsGPSTurnedOn) {
                    getLastLocationAndStartUpdates()
                } else {
                    showSnackBar(R.string.turn_on_gps)
                    val newLocation =
                        locationModel.obtainLocation(AVAILABILITY_STATE.NO_GPS, null, null)
                    if (newLocation != null) {
                        locationModel.locationData.postValue(newLocation)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocationAndStartUpdates() {

        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val location: Location = task.result
                val newLocation = locationModel.obtainLocation(
                    AVAILABILITY_STATE.SUCCESS,
                    location.latitude, location.longitude
                )
                if (newLocation != null) {
                    locationModel.locationData.postValue(newLocation)
                }
            } else {
                val newLocation = locationModel.obtainLocation(
                    AVAILABILITY_STATE.NULL_LOCATION, null,
                    null
                )
                if (newLocation != null) {
                    locationModel.locationData.postValue(newLocation)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    override fun onDialogPositiveClick(dialog: DialogFragment) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun turnGPSOn(bro: (IsGPSTurnedOn: Boolean) -> Unit) {

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
        settingsClient
            .checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(this) {
                bro(true)
            }
            .addOnFailureListener(this) {
                bro(false)
            }

    }


    override fun onDestroy() {
        try {
            unregisterReceiver(mGpsSwitchStateReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        super.onDestroy()

    }

    private fun showSnackBar(snackStrId: Int) {
        snackBar = Snackbar.make(
            containerLayout, getString(snackStrId),
            LENGTH_INDEFINITE
        )
        snackBar?.anchorView = bottomNavigationView
        snackBar?.show()
    }

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            ACCESS_COARSE_LOCATION
        )


    private fun hasLocationPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale()) {
            val dialog = LocationRationaleDialogFragment()
            dialog.show(supportFragmentManager, "LocationRationaleDialogFragment")
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocationAndStartUpdates()
            } else {
                showSnackBar(R.string.no_location_permission)
                val newLocation = locationModel.obtainLocation(
                    AVAILABILITY_STATE.NO_PERMISSION,
                    null, null
                )
                if (newLocation != null) {
                    locationModel.locationData.postValue(newLocation)
                }

            }
        }
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

}