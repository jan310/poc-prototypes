package com.example.eventproducer.configuration

data class Event(
    val eventType: String,
    val eventData: String,
    val users: List<String>
)
