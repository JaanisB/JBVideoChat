package com.example.jbvideochat.util

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {

    @POST("rtmtoken")
    suspend fun getToken(@Body requestBody: RequestBody): Response<ResponseBody>


}
