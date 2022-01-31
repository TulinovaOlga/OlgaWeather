package com.tulinova.olgaweather.data

import okhttp3.ResponseBody


data class Event<out T>(val status: Status, val data: T?, val error: ResponseBody?) {

    companion object {
        fun <T> loading(): Event<T> {
            return Event(Status.LOADING, null, null)
        }

        fun <T> success(data: T?): Event<T> {
            return Event(Status.SUCCESS, data, null)
        }

        fun <T> error(error: ResponseBody?): Event<T> {
            return Event(Status.ERROR, null, error)
        }


    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}


