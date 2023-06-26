package com.example.rsocketclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.messaging.rsocket.RSocketRequester

@SpringBootApplication
class RsocketClientApplication(private val rSocketRequester: RSocketRequester) {

    @Bean
    fun test() {
        rSocketRequester
            .route("events")
            .data("0001")
            .retrieveFlux(String::class.java)
            .doOnNext {
                val pushNotification = ObjectMapper().readValue(it, PushNotification::class.java)
                println(pushNotification.toString())
            }
            .subscribe()
    }

}

fun main(args: Array<String>) {
    runApplication<RsocketClientApplication>(*args)
}
