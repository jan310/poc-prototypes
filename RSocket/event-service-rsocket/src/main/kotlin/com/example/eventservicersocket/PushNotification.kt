package com.example.eventservicersocket

data class PushNotification(
    val timestamp: Long,
    val eventType: String,
    val eventData: String
)
