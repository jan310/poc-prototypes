package com.example.eventservicersocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Controller
class EventController {

    private var sink: Sinks.Many<Event> = Sinks.many().multicast().onBackpressureBuffer()
    private val objectMapper = ObjectMapper()

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val eventObject = objectMapper.readValue(event, Event::class.java)
        sink.tryEmitNext(eventObject)
    }

    @MessageMapping(value = ["events"])
    fun getEvents(@Payload userId: String): Flux<String> {
        if (sink.currentSubscriberCount() == 0) sink = Sinks.many().multicast().onBackpressureBuffer()
        return sink.asFlux()
            .filter { it.users.contains(userId) }
            .map { objectMapper.writeValueAsString(it.toPushNotification()) }
    }

}