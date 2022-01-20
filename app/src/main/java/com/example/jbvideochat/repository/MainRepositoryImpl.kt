package com.example.jbvideochat.repository

import android.util.Log
import com.example.jbvideochat.model.Token
import com.example.jbvideochat.util.APIService
import com.example.jbvideochat.util.Resource
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val apiService: APIService
) : MainRepository {

    override suspend fun getToken(userName: String?): Resource<Token?> {

        Resource.Loading(null)

        return try {

            val jsonObject = JSONObject()
            jsonObject.put("user", userName)
            val jsonObjectString = jsonObject.toString()

            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            // Do the POST request and get response
            val response = apiService.getToken(requestBody)
            val result = response.body()

            if (response.isSuccessful) {

                Resource.Success(result)


            } else {
                val errorMessage = response.errorBody().toString()
                Resource.Error(null, "Failed to get retrofit response, because $errorMessage")
            }

        } catch (e: Exception) {
            Resource.Error(null, "Unknown error!")
        }
    }
}