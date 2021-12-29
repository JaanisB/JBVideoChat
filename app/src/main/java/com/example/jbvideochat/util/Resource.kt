package com.example.jbvideochat.util

import java.lang.Exception

// A generic class that contains data and status about loading this data.
sealed class Resource<T>(
    val data: T?,
    val message: String?
) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(data: T?, message: String) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data, null)
}
