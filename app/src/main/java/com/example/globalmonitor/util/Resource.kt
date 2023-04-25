package com.example.globalmonitor.util


sealed class Resource<out T>(val status : Status , val data: T?, val message : String?) {
    class Success<T>(data : T?) : Resource<T>(Status.SUCCESS, data, null)
    class Error<T>(message: String?, data: T?) : Resource<T>(Status.ERROR, data, message)
    class Loading<T>(data: T?, val isLoading: Boolean = true) : Resource<T>(Status.LOADING, null, null)
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
