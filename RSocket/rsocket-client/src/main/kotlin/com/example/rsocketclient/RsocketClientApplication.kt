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
        //testEventServiceRequestStream() //In RsocketClientConfig.kt Port: 80
        testEventServiceChannel()       //In RsocketClientConfig.kt Port: 80
        //testCatchUpService()              //In RsocketClientConfig.kt Port: 81
    }

    fun testEventServiceRequestStream() {
        val requestPayload1 = RequestPayload1(jwt, listOf("task_completed", "task_deleted"))

        rSocketRequester
            .route("event-service-request-stream")
            .data(ObjectMapper().writeValueAsString(requestPayload1))
            .retrieveFlux(String::class.java)
            .doOnNext { println(it) }
            .subscribe()
    }

    fun testEventServiceChannel() {
        val requestPayload1Stream = Flux.just(
            RequestPayload1(jwt, listOf("task_completed", "task_deleted")),
            RequestPayload1(jwt, listOf("task_completed")),
            RequestPayload1(jwt, listOf("task_deleted"))
        ).delayElements(Duration.ofSeconds(6))

        rSocketRequester
            .route("event-service-channel")
            .data(requestPayload1Stream.map { ObjectMapper().writeValueAsString(it) })
            .retrieveFlux(String::class.java)
            .doOnNext { println(it) }
            .subscribe()
    }

    fun testCatchUpService() {
        val requestPayload2 = RequestPayload2(
            jwt = jwt,
            eventTypes = listOf("task_completed", "task_deleted"),
            lastOffsets = listOf(PartitionOffset(0,20))
        )
        rSocketRequester
            .route("catch-up-service")
            .data(ObjectMapper().writeValueAsString(requestPayload2))
            .retrieveFlux(String::class.java)
            .doOnNext { println(it) }
            .subscribe()
    }

}

fun main(args: Array<String>) {
    runApplication<RsocketClientApplication>(*args)
}
