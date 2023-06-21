package com.example.eventservicesse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventServiceSseApplication

fun main(args: Array<String>) {
	runApplication<EventServiceSseApplication>(*args)
}
