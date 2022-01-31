package com.tulinova.olgaweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tulinova.olgaweather.api.ApiInterface
import com.tulinova.olgaweather.data.Event
import com.tulinova.olgaweather.api.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

abstract class BaseViewModel : ViewModel() {

    var api: ApiInterface = NetworkService.create()

    fun <T> requestWithLiveData(
        liveData: MutableLiveData<Event<T>>,
        request: suspend () -> Response<T>
    ) {

        liveData.postValue(Event.loading())

        this.viewModelScope.launch (Dispatchers.IO) {
            try {
                val response = request.invoke()
                if (response.isSuccessful ) {
                    liveData.postValue(Event.success(response.body()))
               } else{
                   liveData.postValue(Event.error(response.errorBody()))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                liveData.postValue(Event.error(null))
            }
        }
    }


}