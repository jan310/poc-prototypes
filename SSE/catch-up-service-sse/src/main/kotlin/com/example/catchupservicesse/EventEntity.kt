package com.example.catchupservicesse

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "events")
data class EventEntity(
    @Id
    val id: String?,
    val timestamp: Long,
    val eventType: String,
    val eventData: String,
    val users: List<String>
) {
    fun toPushNotification(): PushNotification {
        return PushNotification(timestamp, eventType, eventData)
    }
}
