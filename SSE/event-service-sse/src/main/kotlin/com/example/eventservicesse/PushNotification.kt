package com.example.eventservicesse

data class PushNotification(
    val timestamp: Long,
    val eventType: String,
    val eventData: String
)
