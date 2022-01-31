package com.tulinova.olgaweather.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tulinova.olgaweather.data.AVAILABILITY_STATE
import com.tulinova.olgaweather.data.LocationModel
import kotlin.math.abs

private const val DEFAULT_LATITUDE = 48.85f
private const val DEFAULT_LONGITUDE = 2.35f
private const val comparisonThreshold = 0.01

private const val LATITUDE_KEY = "latitude_default"
private const val LONGITUDE_KEY = "longitude_default"

private const val PREFERENCE_FILE = "com.tulinova.olgaweather.PREFERENCE_FILE"

class LocationViewModel(application: Application) : AndroidViewModel(application) {
     var applicationContext = application
     val locationData = MutableLiveData<LocationModel>()

     fun obtainLocation(state: AVAILABILITY_STATE, newLatitude: Double?, newLongitude: Double?):LocationModel?{
          val value = locationData.value

          if (newLatitude == null || newLongitude == null) {
               if (value != null) {
                    return LocationModel(state, value.latitude, value.longitude)
               } else {
                    val prefs: SharedPreferences =
                         applicationContext.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
                    return LocationModel(
                         state,
                         prefs.getFloat(LATITUDE_KEY, DEFAULT_LATITUDE).toDouble(),
                         prefs.getFloat(LONGITUDE_KEY, DEFAULT_LONGITUDE).toDouble()
                    )
               }

          } else if (value == null || shouldUpdateLocation(
                    value.latitude, newLatitude,
                    value.longitude, newLongitude
               )) {
               val prefs: SharedPreferences =
                    applicationContext.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
               prefs.edit {
                    putFloat(LATITUDE_KEY, newLatitude.toFloat())
                    putFloat(LONGITUDE_KEY, newLongitude.toFloat())
                    apply()
               }

               return LocationModel(state, newLatitude, newLongitude)
          } else {
               return null
          }
     }

     private fun shouldUpdateLocation(
          oldLatitude: Double?, newLatitude: Double?,
          oldLongitude: Double?, newLongitude: Double?
     ): Boolean {

          if (oldLatitude == null || oldLongitude == null) {
               return true
          }
          if (newLatitude == null || newLongitude == null) {
               return false
          }

          return abs(oldLatitude - newLatitude) > comparisonThreshold ||
                  abs(oldLongitude - newLongitude) > comparisonThreshold
     }
}