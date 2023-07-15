package com.example.rsocketclient

data class RequestPayload(
    val jwt: String,
    val topics: List<String>
)
