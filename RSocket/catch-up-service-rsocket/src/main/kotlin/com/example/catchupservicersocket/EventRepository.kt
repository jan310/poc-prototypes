package com.example.catchupservicersocket

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface EventRepository: ReactiveMongoRepository<EventEntity,String> {

    fun findByTimestampGreaterThanAndUsersContainingAndEventTypeIn(
        timestamp: Long,
        user: String,
        eventTypes: List<String>
    ): Flux<EventEntity>

}