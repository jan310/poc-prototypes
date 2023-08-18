package com.example.catchupservicersocket

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class EventController(private val eventRepository: EventRepository) {

    private val objectMapper = ObjectMapper()
    private val jwtVerifier = JWT.require(Algorithm.HMAC256("bachelor")).build()

    @KafkaListener(topics = ["events"])
    fun handleTopic1(consumerRecord: ConsumerRecord<String, String>) {
        val partition = consumerRecord.partition()
        val offset = consumerRecord.offset()
        val event = consumerRecord.value()
        val eventObject = objectMapper.readValue(event, Event::class.java)
        eventRepository.save(eventObject.toEventEntity(partition, offset)).subscribe()
    }

    @MessageMapping(value = ["catch-up-service"])
    fun requestStream(@Payload payload: String): Flux<String> {
        val requestPayload = objectMapper.readValue(payload, RequestPayload::class.java)
        val decodedJWT = try {
            jwtVerifier.verify(requestPayload.jwt)
        } catch (e: JWTVerificationException) {
            return Flux.just("Error: Invalid JWT")
        }
        val userId = decodedJWT.getClaim("sub").asString()
        val pushNotifications = mutableListOf<Flux<String>>()
        requestPayload.lastOffsets.forEach { lastOffset ->
            pushNotifications.add(
                eventRepository.findByUsersContainingAndEventTypeInAndPartitionAndOffsetGreaterThan(
                    user = userId,
                    eventTypes = requestPayload.eventTypes,
                    partition = lastOffset.partition,
                    offset = lastOffset.offset
                ).map { eventEntity ->
                    objectMapper.writeValueAsString(eventEntity.toPushNotification())
                }
            )
        }
        return Flux.concat(pushNotifications)
    }

}