package com.study.WikiKafka.controller

import com.study.WikiKafka.service.WikiKafkaServiceProducer
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api-producer/kafka/wiki")
class WikiKafkaController(
    private val service : WikiKafkaServiceProducer
) {
    @PostMapping
    fun create(@RequestParam(value = "topic") topic : String) {
        service.send(topic)
    }
}