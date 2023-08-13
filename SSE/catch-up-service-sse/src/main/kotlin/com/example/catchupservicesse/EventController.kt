package com.example.catchupservicesse

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api")
class EventController(private val eventRepository: EventRepository) {

    val objectMapper = ObjectMapper()
    val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256("bachelor")).build()

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val eventObject = objectMapper.readValue(event, Event::class.java)
        eventRepository.save(eventObject.toEventEntity()).subscribe()
    }

    @PostMapping(value = ["/events"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getEvents(
        @RequestHeader authorization: String,
        @RequestBody requestPayload: RequestPayload
    ): ResponseEntity<Flux<PushNotification>?> {
        val decodedJWT = try {
            jwtVerifier.verify(authorization)
        } catch (e: JWTVerificationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val userId = decodedJWT.getClaim("sub").asString()
        return ResponseEntity.status(HttpStatus.OK).body(
            eventRepository.findByTimestampGreaterThanAndUsersContainingAndEventTypeIn(
                timestamp = requestPayload.timestamp,
                user = userId,
                eventTypes = requestPayload.eventTypes
            ).map { eventEntity -> eventEntity.toPushNotification() }
        )
    }

}