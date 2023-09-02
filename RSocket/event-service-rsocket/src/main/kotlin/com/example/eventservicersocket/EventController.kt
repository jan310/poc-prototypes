package com.example.eventservicersocket

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.kafka.clients.consumer.ConsumerRecord
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
    fun handleTopic1(consumerRecord: ConsumerRecord<String, String>) {
        val eventObjectNode = objectMapper.readTree(consumerRecord.value()) as ObjectNode
        eventObjectNode.put("partition", consumerRecord.partition())
        eventObjectNode.put("offset", consumerRecord.offset())
        val event = objectMapper.convertValue(eventObjectNode, Event::class.java)
        sink.tryEmitNext(event)
    }

    @MessageMapping(value = ["event-service-request-stream"])
    fun requestStream(@Payload payload: String): Flux<String> {
        val requestPayload = objectMapper.readValue(payload, RequestPayload::class.java)
        val decodedJWT = try {
            jwtVerifier.verify(requestPayload.jwt)
        } catch (e: JWTVerificationException) {
            return Flux.just("Error: Invalid JWT")
        }
        val userId = decodedJWT.getClaim("sub").asString()
        return sink.asFlux()
            .filter { event -> event.users.contains(userId) }
            .filter { event -> requestPayload.eventTypes.contains(event.eventType) }
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
                .filter { event -> lastPayloadObject.eventTypes.contains(event.eventType) }
                .map { event -> objectMapper.writeValueAsString(event.toPushNotification()) }
        }
    }

}