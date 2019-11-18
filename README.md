End to end tests for spring KafkaListener
=========================================

This project showcases how the 
[KafkaListener](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/annotation/KafkaListener.html)
belonging to the [spring-kafka](https://spring.io/projects/spring-kafka) library can
be tested in an end-to-end fashion.

The [spring-kafka](https://spring.io/projects/spring-kafka) comes with a few testing 
utilities, but it doesn't provide any utilities for testing the methods
annotated with the [KafkaListener](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/annotation/KafkaListener.html)
annotation. Moreover, it makes use of an embedded Apache Kafka broker, instead of
dockerized Apache Kafka container image artifacts. 

This project makes use of [testcontainers](https://www.testcontainers.org/) library
for spawning before the tests a complete [Confluent](https://www.confluent.io/) ecosystem
of [docker](https://www.docker.com/) container images for artifacts related to Apache Kafka:

- Apache Kafka
- Apache Zookeeper
- Confluent Schema Registry

By using versions for the container images that correspond to the productive Apache Kafka
ecosystem, there is simulated an environment which is very close to the one running in the 
production. This particularity gives even more relevance to the integration/ end-to-end tests
for the kafka listener functionality.  

By having the ability to perform end-to-end tests in a throwaway dockerized environment 
there can be executed with a high certainty common scenarios that the kafka listener service 
is supposed to handle as part of its contract.

Once the Apache Kafka ecosystem is up and running, the topics necessary for the end-to-end
tests are created and also the [AVRO](https://avro.apache.org/) types are registered 
to Confluent Schema Registry docker container. 
After this setup, the rest of the spring beans from Spring's dependency
injection container (including the kafka listeners) are initialized and therefor we can
execute end-to-end tests on top of them.


This proof of concept project offers two end-to-end sample tests:

- `com.findinpath.kafka.listener.UserBookmarkEventAvroListenerTest` : for 
testing the consumption of messages serialized in [AVRO](https://avro.apache.org/) format
- `com.findinpath.kafka.listener.UserBookmarkEventJsonListenerTest` : for 
testing the consumption of messages serialized in `JSON` format 

Run the command

```
mvn clean install
```

for executing the tests from this project.
 
 
## Limitations

Compared to the tests in which the tests in which the 
[KafkaConsumer](https://kafka.apache.org/23/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html)
can be manipulated directly in order to be able to reset the consumer offset
after each test, [spring-kafka](https://spring.io/projects/spring-kafka) hides the 
consumer instance inside the class `org.springframework.kafka.listener.KafkaMessageListenerContainer.listenerConsumer`
with a `private` access. 
Even with extra motivation, when accessing the private field via Java Reflection,
in order to reset its offset, the operations on it will fail because multi-threaded
access on the consumer is not supported (see `org.apache.kafka.clients.consumer.KafkaConsumer.acquire` method).


But even with the limitation of not being able to reset the consumer offset,
it is still quite useful to ensure the fact that the right service is being called to handle
Kafka message sent over the topic (otherwise said, regression test).

 