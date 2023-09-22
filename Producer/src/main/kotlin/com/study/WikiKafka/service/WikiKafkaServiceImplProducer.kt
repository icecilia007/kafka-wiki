package com.study.WikiKafka.service

import com.launchdarkly.eventsource.EventSource
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.net.URI
import java.util.concurrent.TimeUnit

@Service
class WikiKafkaServiceImplProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
): WikiKafkaServiceProducer {
    final val url="https://stream.wikimedia.org/v2/stream/recentchange";

    override fun send(topic: String){
        val eventHandler = WikiChangeHandler(topic, kafkaTemplate);
        val builder =  EventSource.Builder(eventHandler, URI.create(url));
        val eventSource = builder.build()

        //start producer in another thread
        eventSource.start();
        TimeUnit.MINUTES.sleep(4);
    }

}