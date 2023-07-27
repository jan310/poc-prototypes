package com.example.catchupservicesse

data class RequestPayload(
    val timestamp: Long,
    val topics: List<String>
)
