package com.example.jbvideochat.repository

import com.example.jbvideochat.util.Resource

interface MainRepository {

    suspend fun getToken (userName: String?) : Resource<String>
}