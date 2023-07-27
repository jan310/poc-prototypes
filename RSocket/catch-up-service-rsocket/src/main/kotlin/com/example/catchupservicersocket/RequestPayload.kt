package com.example.catchupservicersocket

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestPayload(
    @JsonProperty("jwt") val jwt: String,
    @JsonProperty("timestamp") val timestamp: Long,
    @JsonProperty("topics") val topics: List<String>
)
