package com.study.WikiKafka.service

interface WikiKafkaServiceProducer {
    fun send(topic : String)
}