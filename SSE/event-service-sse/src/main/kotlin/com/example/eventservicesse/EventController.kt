package com.example.eventservicesse

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@RestController
@RequestMapping("/api")
class EventController {
    private var sink: Sinks.Many<Event> = Sinks.many().multicast().onBackpressureBuffer()

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val eventObject = ObjectMapper().readValue(event, Event::class.java)
        sink.tryEmitNext(eventObject)
    }

    @GetMapping(value = ["/events/{userId}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getEvents(@PathVariable userId: String): Flux<PushNotification> {
        //Wenn die Anzahl der sink-Subscriber (Clients, die über diesen Endpunkt Benachrichtigungen erhalten) auf 0
        //sinkt, funktioniert der Sink irgendwie nicht mehr richtig (erneute Client-Verbindungen werden nach dem
        //Öffnen sofort wieder geschlossen). Deshalb wird als Workaround der sink einfach neu initialisiert.
        if (sink.currentSubscriberCount() == 0) sink = Sinks.many().multicast().onBackpressureBuffer()
        return sink.asFlux()
            .filter { it.users.contains(userId) }
            .map { it.toPushNotification() }
    }
}
