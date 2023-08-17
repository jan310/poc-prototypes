package com.example.catchupservicesse

data class RequestPayload(
    val eventTypes: List<String>,
    val lastOffsets: List<PartitionOffset>
)

data class PartitionOffset(
    val partition: Int,
    val offset: Long
)