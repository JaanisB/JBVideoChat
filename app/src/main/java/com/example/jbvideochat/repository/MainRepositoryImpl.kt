package com.example.jbvideochat.repository

import android.util.Log
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

    override suspend fun getToken(userName: String?): Resource<String> {

        Resource.Loading(null)

        return try {

            val jsonObject = JSONObject()
            jsonObject.put("user", userName)
            val jsonObjectString = jsonObject.toString()


            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            // Do the POST request and get response
            val response = apiService.getToken(requestBody)
            if (response.isSuccessful) {

                // Convert raw JSON to pretty JSON using GSON library
                val gson = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = gson.toJson(
                    JsonParser.parseString(
                        response.body()
                            ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                    )
                )

                Log.d("Pretty Printed JSON :", prettyJson)
                Resource.Success(prettyJson)
            } else {
                Resource.Error(null, "Failed to get retrofit response")
            }

        } catch (e: Exception) {
            Resource.Error(null, "Unknown error!")
        }


    }
}