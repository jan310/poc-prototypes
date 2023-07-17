package com.example.rsocketclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.publisher.Flux
import java.time.Duration

@SpringBootApplication
class RsocketClientApplication(private val rSocketRequester: RSocketRequester) {

    private val jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzZXJ2ZXIiLCJzdWIiOiIwMDAxIiwiZXhwIjoxNjk3MjU3ODcyLCJ0b3BpY3MiOlsidGFza19jb21wbGV0ZWQiLCJ0YXNrX2RlbGV0ZWQiXX0.QaHJEwc4O_slvxtRhn6syBqMssmA9B_Eml_zdzgCg-Y"

    @Bean
    fun test() {
        //testRequestStream()
        testChannel()
    }

    fun testRequestStream() {
        val requestPayload = RequestPayload(jwt, listOf("task_completed", "task_deleted"))

        rSocketRequester
            .route("request-stream")
            .data(ObjectMapper().writeValueAsString(requestPayload))
            .retrieveFlux(String::class.java)
            .doOnNext { println(it) }
            .subscribe()
    }

    fun testChannel() {
        val requestPayloadStream = Flux.just(
            RequestPayload(jwt, listOf("task_completed", "task_deleted")),
            RequestPayload(jwt, listOf("task_completed")),
            RequestPayload(jwt, listOf("task_deleted"))
        ).delayElements(Duration.ofSeconds(6))

        rSocketRequester
            .route("channel")
            .data(requestPayloadStream.map { ObjectMapper().writeValueAsString(it) })
            .retrieveFlux(String::class.java)
            .doOnNext { println(it) }
            .subscribe()
    }

}

fun main(args: Array<String>) {
    runApplication<RsocketClientApplication>(*args)
}
