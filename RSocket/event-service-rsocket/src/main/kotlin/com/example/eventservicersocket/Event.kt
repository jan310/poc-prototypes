package com.example.eventservicersocket

import com.fasterxml.jackson.annotation.JsonProperty

data class Event(
    @JsonProperty("eventType") val eventType: String,
    @JsonProperty("eventData") val eventData: String,
    @JsonProperty("users") val users: List<String>,
    @JsonProperty("partition") val partition: Int,
    @JsonProperty("offset") val offset: Long
) {
    fun toPushNotification(): PushNotification {
        return PushNotification(eventType, eventData, partition, offset)
    }
}
