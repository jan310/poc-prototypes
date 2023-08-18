package com.example.catchupservicersocket

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "events")
data class EventEntity(
    @Id
    val id: String?,
    val eventType: String,
    val eventData: String,
    val users: List<String>,
    val partition: Int,
    val offset: Long
) {
    fun toPushNotification(): PushNotification {
        return PushNotification(eventType, eventData, partition, offset)
    }
}
