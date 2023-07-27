package com.example.catchupservicesse

import com.fasterxml.jackson.annotation.JsonProperty

data class Event(
    @JsonProperty("eventType") val eventType: String,
    @JsonProperty("eventData") val eventData: String,
    @JsonProperty("users") val users: List<String>
) {
    fun toEventEntity(): EventEntity {
        return EventEntity(
            id = null,
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            eventData = eventData,
            users = users
        )
    }
}
