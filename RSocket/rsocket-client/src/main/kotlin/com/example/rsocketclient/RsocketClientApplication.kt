package com.example.rsocketclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.messaging.rsocket.RSocketRequester

@SpringBootApplication
class RsocketClientApplication(private val rSocketRequester: RSocketRequester) {

    private val objectMapper = ObjectMapper()

    @Bean
    fun test() {
        val requestPayload = RequestPayload(
            jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXJ2ZXIiLCJzdWIiOiIwMDAxIiwiZXhwIjoxNjk3MjU3ODcyLCJ" +
                    "0b3BpY3MiOlsidGFza19jb21wbGV0ZWQiLCJ0YXNrX2RlbGV0ZWQiXX0.QaHJEwc4O_slvxtRhn6syBqMssmA9B_Eml_zdzgCg-Y",
            topics = listOf("task_completed", "task_deleted")
        )

        rSocketRequester
            .route("events")
            .data(objectMapper.writeValueAsString(requestPayload))
            .retrieveFlux(String::class.java)
            .doOnNext { println(it) }
            .subscribe()
    }

}

fun main(args: Array<String>) {
    runApplication<RsocketClientApplication>(*args)
}
