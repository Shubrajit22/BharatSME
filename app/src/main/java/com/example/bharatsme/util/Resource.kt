package com.example.bharatsme.util

// Adding 'out' here allows Resource<TokenResponse> to be treated as Resource<Any>
// app/util/Resource.kt
sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Error<out T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<out T>(data: T? = null) : Resource<T>(data)
}