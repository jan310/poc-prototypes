package com.example.catchupservicesse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CatchUpServiceSseApplication

fun main(args: Array<String>) {
	runApplication<CatchUpServiceSseApplication>(*args)
}
