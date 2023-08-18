package com.example.catchupservicersocket

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestPayload(
    @JsonProperty("jwt") val jwt: String,
    @JsonProperty("eventTypes") val eventTypes: List<String>,
    @JsonProperty("lastOffsets") val lastOffsets: List<PartitionOffset>
)

data class PartitionOffset(
    @JsonProperty("partition") val partition: Int,
    @JsonProperty("offset") val offset: Long
)