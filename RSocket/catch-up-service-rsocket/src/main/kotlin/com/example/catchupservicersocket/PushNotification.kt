package com.example.catchupservicersocket

data class PushNotification(
    val eventType: String,
    val eventData: String,
    val partition: Int,
    val offset: Long
)
