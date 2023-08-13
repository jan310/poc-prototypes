package com.example.catchupservicesse

data class RequestPayload(
    val timestamp: Long,
    val eventTypes: List<String>
)
