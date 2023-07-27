package com.example.catchupservicersocket

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
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
    fun handleTopic1(event: String) {
        val eventObject = objectMapper.readValue(event, Event::class.java)
        eventRepository.save(eventObject.toEventEntity()).doOnNext { println(it.id) }.subscribe()
    }

    @MessageMapping(value = ["catch-up-service"])
    fun requestStream(@Payload payload: String): Flux<String> {
        val requestPayloadObject = objectMapper.readValue(payload, RequestPayload::class.java)

        val decodedJWT = try {
            jwtVerifier.verify(requestPayloadObject.jwt)
        } catch (e: JWTVerificationException) {
            return Flux.just("Error: Invalid JWT")
        }

        val userId = decodedJWT.getClaim("sub").asString()

        return eventRepository.findByTimestampGreaterThanAndUsersContainingAndEventTypeIn(
            requestPayloadObject.timestamp,
            userId,
            requestPayloadObject.topics
        ).map { objectMapper.writeValueAsString(it.toPushNotification()) }
    }

}