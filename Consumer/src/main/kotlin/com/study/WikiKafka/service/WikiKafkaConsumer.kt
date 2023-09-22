package com.study.WikiKafka.service

import com.google.gson.JsonParser
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.opensearch.action.admin.indices.create.CreateIndexRequest
import org.opensearch.action.bulk.BulkRequest
import org.opensearch.action.index.IndexRequest
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.opensearch.client.indices.GetIndexRequest
import org.opensearch.common.xcontent.XContentType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.net.URI
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration


@Component
class WikiKafkaConsumer {
    @KafkaListener(topics = ["wikimedia.recentchange"] ,groupId = "consumer-opensearch-demo")
    fun consumeMessage(consumer : KafkaConsumer<String, String>
    ){
        val openSearchClient = createOpenSearchClient()
        val mainThread = Thread.currentThread()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                println("Detected a shutdown, let's exit by calling consumer.wakeup()...")
                consumer.wakeup()
                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        })
        try {
            openSearchClient.use {
                consumer.use {
                    val indexExists = openSearchClient.indices()
                        .exists(GetIndexRequest("wikimedia"), RequestOptions.DEFAULT)
                    if (!indexExists) {
                        val createIndexRequest =
                            CreateIndexRequest("wikimedia")
                        openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT)
                        println("The Wikimedia Index has been created!")
                    } else {
                        println("The Wikimedia Index already exits")
                    }

                    // we subscribe the consumer this is the topic
                    consumer.subscribe(setOf("wikimedia.recentchange"))
                    while(true){
                        val records: ConsumerRecords<String, String> = consumer.poll(3000.milliseconds.toJavaDuration())
                        val recordCount = records.count()
                        println("Received $recordCount record(s)")
                        val bulkRequest = BulkRequest()
                        for (record in records) {
                            sendRecordOpenSearch(record, bulkRequest)
                        }
                        if (bulkRequest.numberOfActions() > 0) {
                            val bulkResponse = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT)
                            println("Inserted " + bulkResponse.items.size + " record(s).")
                            try {
                                Thread.sleep(1000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                            // commit offsets after the batch is consumed
                            consumer.commitSync()
                            println("Offsets have been committed!")
                        }

                    }
                }
            }

        }catch (e : WakeupException){
            println("Consumer is starting to shut down")
        }catch(e : Exception){
            println("Unexpected exception in the consumer $e")
        }finally {
            consumer.close(); // close the consumer, this will also commit offsets
            openSearchClient.close();
            println("The consumer is now gracefully shut down");
        }
    }

    private fun sendRecordOpenSearch(record: ConsumerRecord<String, String>, bulkRequest: BulkRequest) {
        // send the record into OpenSearch
        try {

            val id: String = extractId(record.value())
            val indexRequest = IndexRequest("wikimedia")
                .source(record.value(), XContentType.JSON)
                .id(id)

//                        IndexResponse response = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);
            bulkRequest.add(indexRequest)

//                        log.info(response.getId());
        } catch (e: Exception) {
        }
    }
    fun createOpenSearchClient(): RestHighLevelClient {
        val connString = "http://localhost:9200"
//        val connString = "https://c9p5mwld41:45zeygn9hy@kafka-course-2322630105.eu-west-1.bonsaisearch.net:443";

        // we build a URI from the connection string
        val restHighLevelClient: RestHighLevelClient
        val connUri: URI = URI.create(connString)
        // extract login information if it exists
        val userInfo = connUri.getUserInfo()
         if (userInfo == null) {
            // REST client without security
            restHighLevelClient = RestHighLevelClient(RestClient.builder(HttpHost(connUri.host, connUri.port, "http")))
        } else {
            // REST client with security
            val auth = userInfo.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val cp: CredentialsProvider = BasicCredentialsProvider()
            cp.setCredentials(
                AuthScope.ANY, UsernamePasswordCredentials(
                    auth[0],
                    auth[1]
                )
            )
             restHighLevelClient = RestHighLevelClient(
                RestClient.builder(HttpHost(connUri.host, connUri.port, connUri.scheme))
                    .setHttpClientConfigCallback { httpAsyncClientBuilder: HttpAsyncClientBuilder ->
                        httpAsyncClientBuilder.setDefaultCredentialsProvider(cp)
                            .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy())
                    })
        }
        return restHighLevelClient
    }
    private fun extractId(json: String): String {
        return JsonParser.parseString(json)
            .getAsJsonObject()
            .get("meta")
            .getAsJsonObject()
            .get("id")
            .getAsString()
    }
}