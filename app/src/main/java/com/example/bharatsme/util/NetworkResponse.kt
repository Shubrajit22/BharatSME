package com.example.bharatsme.util

// Put this in your 'util' or 'network' package
sealed class NetworkResponse<out T> {
    data class Success<T>(val body: T) : NetworkResponse<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResponse<Nothing>()
}