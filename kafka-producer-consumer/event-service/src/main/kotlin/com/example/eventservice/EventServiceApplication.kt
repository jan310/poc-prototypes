package com.example.eventservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class EventServiceApplication {

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val jsonNode = ObjectMapper().readTree(event)
        println(jsonNode.get("eventData").asText())
    }

}

fun main(args: Array<String>) {
    runApplication<EventServiceApplication>(*args)
}
