package com.example.eventservicemqtt

import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.client.MqttConnectionOptionsBuilder
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class MqttClientConfig(private val env: Environment) {

    @Bean
    fun mqttAsyncClient(): MqttAsyncClient {
        val broker = "tcp://${env.getProperty("mqtt-broker")}"
        val clientId = env.getProperty("mqtt-client-id")
        val client =  MqttAsyncClient(broker, clientId, MemoryPersistence())
        val connectionOptions = MqttConnectionOptionsBuilder().cleanStart(false).sessionExpiryInterval(3600).keepAliveInterval(10).build()
        client.connect(connectionOptions).waitForCompletion()
        return client
    }

}