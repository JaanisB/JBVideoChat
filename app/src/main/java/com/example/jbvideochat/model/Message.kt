package com.example.jbvideochat.model

data class Message(
    val isReceived: Boolean,
    val user: String,
    val message: String
)
