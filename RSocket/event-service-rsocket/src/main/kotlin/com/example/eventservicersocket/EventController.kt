package com.example.eventservicersocket

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration

@Controller
class EventController {

    private var sink: Sinks.Many<Event> = Sinks.many().replay().limit(Duration.ZERO)
    private val objectMapper = ObjectMapper()
    private val jwtVerifier = JWT.require(Algorithm.HMAC256("bachelor")).build()

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val eventObject = objectMapper.readValue(event, Event::class.java)
        sink.tryEmitNext(eventObject)
    }

    @MessageMapping(value = ["event-service-request-stream"])
    fun requestStream(@Payload payload: String): Flux<String> {
        val requestPayloadObject = objectMapper.readValue(payload, RequestPayload::class.java)

        val decodedJWT = try {
            jwtVerifier.verify(requestPayloadObject.jwt)
        } catch (e: JWTVerificationException) {
            return Flux.just("Error: Invalid JWT")
        }

        val userId = decodedJWT.getClaim("sub").asString()
        return sink.asFlux()
            .filter { event -> event.users.contains(userId) }
            .filter { event -> requestPayloadObject.topics.contains(event.eventType) }
            .map { event -> objectMapper.writeValueAsString(event.toPushNotification()) }
    }

    @MessageMapping(value = ["event-service-channel"])
    fun channel(@Payload payload: Flux<String>): Flux<String> {
        return payload.switchMap { lastPayload ->
            val lastPayloadObject = objectMapper.readValue(lastPayload, RequestPayload::class.java)

            val decodedJWT = try {
                jwtVerifier.verify(lastPayloadObject.jwt)
            } catch (e: JWTVerificationException) {
                return@switchMap Flux.just("Error: Invalid JWT")
            }

            val userId = decodedJWT.getClaim("sub").asString()
            sink.asFlux()
                .filter { event -> event.users.contains(userId) }
                .filter { event -> lastPayloadObject.topics.contains(event.eventType) }
                .map { event -> objectMapper.writeValueAsString(event.toPushNotification()) }
        }
    }


}