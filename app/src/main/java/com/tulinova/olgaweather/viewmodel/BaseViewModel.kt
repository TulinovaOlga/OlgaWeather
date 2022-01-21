package com.tulinova.olgaweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tulinova.olgaweather.api.ApiInterface
import com.tulinova.olgaweather.api.CustomError
import com.tulinova.olgaweather.api.Event
import com.tulinova.olgaweather.api.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    var api: ApiInterface = NetworkService.create()

    fun <T> requestWithLiveData(
        liveData: MutableLiveData<Event<T>>,
        request: suspend () -> T) {

        liveData.postValue(Event.loading())

        this.viewModelScope.launch (Dispatchers.IO) {
            try {
                val response = request.invoke()
                if (response != null) {
                    liveData.postValue(Event.success(response))
               } else{
                   //TODO: check is it server error or no network error
                   liveData.postValue(Event.error(CustomError(102, "Some error on server")))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                liveData.postValue(Event.error(null))
            }
        }
    }


}