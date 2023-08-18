package com.example.rsocketclient

data class RequestPayload2(
    val jwt: String,
    val eventTypes: List<String>,
    val lastOffsets: List<PartitionOffset>
)

data class PartitionOffset(
    val partition: Int,
    val offset: Long
)
