package com.example.eventproducer

import com.example.eventproducer.configuration.Event
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@SpringBootApplication
@EnableScheduling
class EventProducerApplication(private val kafkaTemplate: KafkaTemplate<String, String>) {

    var counter = 0

    @Scheduled(fixedRate = 1000)
    fun publishEvent() {
        //kafkaTemplate.send("events", counter%2, "event-key", generateEvent())
        kafkaTemplate.send("events", generateEvent())
        println("Message sent")
    }

    fun generateEvent() : String {
        val event = if (counter % 2 == 0)
            Event("task_completed", "Message_${counter++}", listOf("0001", "0002", "0003")) else
            Event("task_deleted", "Message_${counter++}", listOf("0001"))
        return ObjectMapper().writeValueAsString(event)
    }

}

fun main(args: Array<String>) {
    runApplication<EventProducerApplication>(*args)
}
