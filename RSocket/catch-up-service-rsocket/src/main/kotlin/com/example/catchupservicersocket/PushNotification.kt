package com.example.catchupservicersocket

data class PushNotification(
    val timestamp: Long,
    val eventType: String,
    val eventData: String
)
