package com.example.eventservicersocket

data class PushNotification(
    val eventType: String,
    val eventData: String,
    val partition: Int,
    val offset: Long
)
