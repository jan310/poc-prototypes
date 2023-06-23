package com.example.eventservicemqtt

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class EventServiceMqttApplication(private val mqttAsyncClient: MqttAsyncClient) {

    @KafkaListener(topics = ["events"])
    fun handleTopic1(event: String) {
        val objectMapper = ObjectMapper()
        val eventObject = objectMapper.readValue(event, Event::class.java)

        val message = MqttMessage(objectMapper.writeValueAsBytes(eventObject.toPushNotification()))
        message.qos = 2

        val mqttProperties = MqttProperties()
        mqttProperties.messageExpiryInterval = 3600
        message.properties = mqttProperties

        mqttAsyncClient.publish(eventObject.eventType, message)
    }

}

fun main(args: Array<String>) {
    runApplication<EventServiceMqttApplication>(*args)
}
