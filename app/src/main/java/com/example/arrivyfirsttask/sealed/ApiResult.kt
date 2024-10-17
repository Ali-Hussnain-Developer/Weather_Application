package com.example.arrivyfirsttask.sealed

// ApiResult.kt
sealed class ApiResult<out T> {

    // Represents a loading state of the API call
    data object Loading : ApiResult<Nothing>()

    // Represents a successful API call with data
    data class Success<out T>(val data: T) : ApiResult<T>()

    // Represents an error state of the API call
    data class Error(val exception: Throwable) : ApiResult<Nothing>()
}

