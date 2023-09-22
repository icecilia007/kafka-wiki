package com.study.WikiKafka.service

import com.google.gson.Gson
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import org.springframework.kafka.core.KafkaTemplate

class WikiChangeHandler(
    private val topic : String,
    private val kafkaTemplate: KafkaTemplate<String, String>
) : EventHandler{
    override fun onOpen() {
        //nothing
    }

    override fun onClosed() {
        kafkaTemplate.destroy()
    }

    override fun onMessage(event: String?, messageEvent: MessageEvent?) {
        if (messageEvent != null) {
            kafkaTemplate.send(topic, messageEvent.data)
        }
    }

    override fun onComment(comment: String?) {
        //nothing
    }

    override fun onError(t: Throwable?) {
        println("Error in Stream Reading $t")
    }
}