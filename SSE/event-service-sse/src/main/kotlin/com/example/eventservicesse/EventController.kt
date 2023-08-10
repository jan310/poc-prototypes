package com.example.eventservicesse

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration

@RestController
@RequestMapping("/api")
class EventController {

    val objectMapper = ObjectMapper()
    val sink: Sinks.Many<Event> = Sinks.many().replay().limit(Duration.ZERO)
    val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256("bachelor")).build()

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val eventObject = objectMapper.readValue(event, Event::class.java)
        sink.tryEmitNext(eventObject)
    }

    @PostMapping(value = ["/events"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getEvents(
        @RequestHeader authorization: String,
        @RequestBody requestPayload: RequestPayload
    ): ResponseEntity<Flux<ServerSentEvent<PushNotification>>?> {
        val decodedJWT = try {
            jwtVerifier.verify(authorization)
        } catch (e: JWTVerificationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val userId = decodedJWT.getClaim("sub").asString()
        return ResponseEntity.status(HttpStatus.OK).body(
            sink.asFlux()
                .filter { event -> event.users.contains(userId) }
                .filter { event -> requestPayload.eventTypes.contains(event.eventType) }
                .map { event ->
                    ServerSentEvent.builder(event.toPushNotification()).event(event.eventType).build()
                }
        )
    }

}

//Sinks.Many ist wie Flux ein Publisher. Der Unterschied ist, dass Sinks.Many gleichzeitig mehrere Subscriber haben
//kann. Außerdem ist Sinks.Many mutable, es können also zu jeder Zeit neue Werte zum Stream hinzugefügt werden, selbst
//nachdem Clients subscribed haben
