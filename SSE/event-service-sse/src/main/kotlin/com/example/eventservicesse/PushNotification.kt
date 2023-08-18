package com.example.eventservicesse

data class PushNotification(
    val eventType: String,
    val eventData: String,
    val partition: Int,
    val offset: Long
)
