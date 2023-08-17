package com.example.catchupservicesse

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface EventRepository: ReactiveMongoRepository<EventEntity,String> {

    fun findByUsersContainingAndEventTypeInAndPartitionAndOffsetGreaterThan(
        user: String,
        eventTypes: List<String>,
        partition: Int,
        offset: Long
    ): Flux<EventEntity>

}