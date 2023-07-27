package com.example.rsocketclient

data class RequestPayload2(
    val jwt: String,
    val timestamp: Long,
    val topics: List<String>
)
