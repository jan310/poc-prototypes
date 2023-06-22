package com.example.eventservicemqtt

import com.fasterxml.jackson.annotation.JsonProperty

data class Event(
    @JsonProperty("eventType") val eventType: String,
    @JsonProperty("eventData") val eventData: String,
    @JsonProperty("users") val users: List<String>
) {
    fun toPushNotification(): PushNotification {
        return PushNotification(eventType, eventData)
    }
}
