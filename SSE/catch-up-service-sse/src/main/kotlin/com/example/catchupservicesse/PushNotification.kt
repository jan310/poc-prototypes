package com.example.catchupservicesse

data class PushNotification(
    val timestamp: Long,
    val eventType: String,
    val eventData: String
)
