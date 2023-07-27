package com.example.rsocketclient

import com.fasterxml.jackson.annotation.JsonProperty

data class PushNotification(
    @JsonProperty("eventType") val eventType: String,
    @JsonProperty("eventData") val eventData: String
)//
