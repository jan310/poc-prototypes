package com.example.eventproducer.configuration

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(private val env: Environment) {

    @Bean
    fun kafkaAdmin() : KafkaAdmin {
        val config = mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to env.getProperty("spring.kafka.bootstrap-servers"))
        return KafkaAdmin(config)
    }

    @Bean
    fun topic() : NewTopic {
        return NewTopic("events", 2, 1)
    }

}