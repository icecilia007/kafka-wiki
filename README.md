# <h1 align="center">Kafka Project Troubleshooting</h1>

  In this Kafka project, I've built a Kafka-based application inspired by a Udemy course. The project consists of a producer and a consumer, both containerized using Docker Compose. Before working with this application, there are some potential issues and requirements you should be aware of:
## ✅ Prerequisites
  Make sure to have docker running in you machine, if you dont have docker installed, follow this [documentation](https://docs.docker.com/get-docker/)
## :warning: Possible Issues:

- **Not Running the Producer's Docker Compose:** Make sure to run the Docker Compose for the producer component. Without it, the necessary Kafka infrastructure won't be available.

- **Blocked Ports:** Ensure that all required ports are open. Kafka relies on specific ports for communication, so blocked ports can cause issues.
  ### Producer ports
  -Port 2181: ZooKeeper
  -Port 9092: Kafka (Plaintext Listener)
  -Port 29092: Kafka (Outra Listener)
  -Port 9999: JMX para Kafka (Monitoramento)
  -Port 8083: Schema Registry
  -Port 8080: Conduktor Platform
  ### Consumer ports
  -Port 9200: OpenSearch (HTTP API)
  -Port 9600: OpenSearch Performance Analyzer
  -Port 5601: OpenSearch Dashboards (Kibana)

- **Running Consumer Before Producer:** The consumer component relies on the Kafka infrastructure provided by the producer. Therefore, you should run the producer's Docker Compose first, as the consumer won't function correctly without it.

## :computer: Access to Conducktor Platform:

  To access the Conducktor platform, you'll need to log in with the following credentials, which are defined in the producer's Docker Compose configuration:

- **Username:** admin@conduktor.io
- **Password:** admin

## :question: Need Help or Have Questions?

  If you encounter any issues, have questions, or need further assistance, please don't hesitate to reach out. I'm here to help you make the most of this Kafka project. Feel free to get in touch!

Happy Kafkaing! :rocket:
