package com.findinpath.kafka.config;

import com.findinpath.kafka.dto.avro.UserBookmarkEvent;
import com.findinpath.kafka.testcontainers.KafkaTestContainers;
import com.findinpath.kafka.testcontainers.SchemaRegistryContainer;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for throwaway Apache Kafka ecosystem container images to be used for test
 * purposes.
 *
 * <b>NOTE</b> that while the  {@link #kafkaTestContainers(String, String)} bean
 * from this configuration class is instantiated, there are also some extra steps performed:
 *
 * <ul>
 *   <li>the Apache Kafka topics needed in the tests are created</li>
 *   <li>the AVRO types needed in the tests are registered in the Schema Registry</li>
 * </ul>
 */
@Configuration
public class KafkaDockerConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDockerConfiguration.class);

	private static AdminClient createAdminClient(KafkaTestContainers kafkaTestContainers) {
		var properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				kafkaTestContainers.getKafkaContainer().getBootstrapServers());

		return KafkaAdminClient.create(properties);
	}

	private static void registerSchemaRegistryTypes(SchemaRegistryContainer schemaRegistryContainer)
			throws IOException, RestClientException {
		// the UserBookmarkEvent avro class needs to  be registered in the Confluent schema registry
		// for setting up the tests.
		LOGGER.info("Registering the types in the Schema Registry");
		var schemaRegistryClient = new CachedSchemaRegistryClient(
				schemaRegistryContainer.getServiceURL(), 100);
		schemaRegistryClient.register(UserBookmarkEvent.getClassSchema().getFullName(),
				UserBookmarkEvent.getClassSchema());
	}

	private static void createTopics(KafkaTestContainers kafkaTestContainers,
			String userBookmarkEventJsonTopic,
			String userBookmarkEventAvroTopic) throws InterruptedException, ExecutionException {
		try (var adminClient = createAdminClient(kafkaTestContainers)) {
			short replicationFactor = 1;
			int partitions = 1;

			LOGGER.info("Creating topics in Apache Kafka");
			adminClient.createTopics(Arrays.asList(
					new NewTopic(userBookmarkEventJsonTopic, partitions, replicationFactor),
					new NewTopic(userBookmarkEventAvroTopic, partitions, replicationFactor)
			)).all().get();
		}
	}

	@Bean
	public KafkaTestContainers kafkaTestContainers(
			@Value("${kafka.userBookmarkEventsJson.topic}") String userBookmarkEventJsonTopic,
			@Value("${kafka.userBookmarkEventsAvro.topic}") String userBookmarkEventAvroTopic
	) throws Exception {
		var kafkaTestContainers = new KafkaTestContainers();

		createTopics(kafkaTestContainers, userBookmarkEventJsonTopic, userBookmarkEventAvroTopic);
		registerSchemaRegistryTypes(kafkaTestContainers.getSchemaRegistryContainer());
		return kafkaTestContainers;
	}

	@Bean
	public KafkaEcosystemProperties kafkaEcosystemProperties(
			KafkaTestContainers kafkaTestContainers) {
		return new KafkaEcosystemProperties(
				kafkaTestContainers.getKafkaContainer().getBootstrapServers(),
				kafkaTestContainers.getSchemaRegistryContainer().getServiceURL()
		);
	}

	@Bean
	public AdminClient adminClient(KafkaTestContainers kafkaTestContainers) {
		return createAdminClient(kafkaTestContainers);
	}
}
