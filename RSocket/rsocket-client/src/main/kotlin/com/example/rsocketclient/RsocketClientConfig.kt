package com.example.rsocketclient

import io.rsocket.core.RSocketConnector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.util.MimeTypeUtils
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration


@Configuration
class RsocketClientConfig {

    @Bean
    fun getRSocketRequester(): RSocketRequester {
        val builder = RSocketRequester.builder()
        return builder
            .rsocketConnector { rSocketConnector: RSocketConnector ->
                rSocketConnector.reconnect(
                    Retry.fixedDelay(
                        2,
                        Duration.ofSeconds(2)
                    )
                )
            }
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
            .websocket(URI("ws://localhost:80/rsocket"))
    }

}