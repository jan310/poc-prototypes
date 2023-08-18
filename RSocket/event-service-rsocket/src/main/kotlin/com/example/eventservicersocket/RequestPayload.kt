package com.example.eventservicersocket

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestPayload(
    @JsonProperty("jwt") val jwt: String,
    @JsonProperty("eventTypes") val eventTypes: List<String>
)
