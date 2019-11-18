package com.findinpath.kafka.config;

import com.findinpath.kafka.dto.UserBookmarkEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Configuration class used for the consumer factories used in the classes containing ( with methods
 * annotated with `@KafkaListener` annotation) kafka listener functionality.
 */
@Configuration
@EnableKafka
public class KafkaConfiguration {

	private static <K, V> ConsumerFactory<K, V> avroConsumerFactory(
			KafkaEcosystemProperties kafkaEcosystemProperties,
			String consumerGroupId) {
		return new DefaultKafkaConsumerFactory<>(constructAvroConsumerConfig(
				kafkaEcosystemProperties, consumerGroupId));
	}

	private static <K, V> ConsumerFactory<K, V> jsonConsumerFactory(
			KafkaEcosystemProperties kafkaEcosystemProperties,
			String consumerGroupId) {
		return new DefaultKafkaConsumerFactory<>(
				constructConsumerConfig(kafkaEcosystemProperties, consumerGroupId));
	}

	private static Map<String, Object> constructBasicConsumerProperties(String consumerGroupId,
			String bootstrapServers) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		return properties;
	}

	private static Map<String, Object> constructAvroConsumerConfig(
			KafkaEcosystemProperties kafkaEcosystemProperties,
			String consumerGroupId) {
		var properties = constructBasicConsumerProperties(consumerGroupId,
				kafkaEcosystemProperties.getBootstrapServers());
		properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
				kafkaEcosystemProperties.getSchemaRegistryUrl());
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
		return properties;
	}

	private static Map<String, Object> constructConsumerConfig(
			KafkaEcosystemProperties kafkaEcosystemProperties,
			String consumerGroupId) {
		Map<String, Object> properties = constructBasicConsumerProperties(consumerGroupId,
				kafkaEcosystemProperties.getBootstrapServers());
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		properties.put(JsonDeserializer.TYPE_MAPPINGS,
				"userBookmarkEvent:com.findinpath.kafka.dto.UserBookmarkEvent");
		return properties;
	}

	/**
	 * Kafka listener container factory dedicated for JSON Kafka Listeners.
	 *
	 * @param kafkaEcosystemProperties contains the addresses of the Apache Kafka ecosystem artifacts
	 * @param numberOfConsumers the number of consumers for the topic
	 * @param consumerGroupId the consumer group id
	 * @return Kafka listener container factory dedicated for JSON Kafka Listeners.
	 */
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, UserBookmarkEvent>
	userBookmarkEventJsonKafkaListenerContainerFactory(
			KafkaEcosystemProperties kafkaEcosystemProperties,
			@Value("${kafka.userBookmarkEventsJson.numberOfConsumers}") int numberOfConsumers,
			@Value("${kafka.userBookmarkEventsJson.consumerGroupId}") String consumerGroupId
	) {
		ConcurrentKafkaListenerContainerFactory<String, UserBookmarkEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConcurrency(numberOfConsumers);
		factory.setConsumerFactory(jsonConsumerFactory(kafkaEcosystemProperties, consumerGroupId));

		return factory;
	}

	/**
	 * Kafka listener container factory dedicated for AVRO Kafka Listeners.
	 *
	 * @param kafkaEcosystemProperties contains the addresses of the Apache Kafka ecosystem artifacts
	 * @param numberOfConsumers the number of consumers for the topic
	 * @param consumerGroupId the consumer group id
	 * @return Kafka listener container factory dedicated for AVRO Kafka Listeners.
	 */
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, com.findinpath.kafka.dto.avro.UserBookmarkEvent>
	userBookmarkEventAvroKafkaListenerContainerFactory(
			KafkaEcosystemProperties kafkaEcosystemProperties,
			@Value("${kafka.userBookmarkEventsAvro.numberOfConsumers}") int numberOfConsumers,
			@Value("${kafka.userBookmarkEventsAvro.consumerGroupId}") String consumerGroupId) {
		ConcurrentKafkaListenerContainerFactory<String, com.findinpath.kafka.dto.avro.UserBookmarkEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConcurrency(numberOfConsumers);
		factory.setConsumerFactory(avroConsumerFactory(kafkaEcosystemProperties, consumerGroupId));

		return factory;
	}


}
