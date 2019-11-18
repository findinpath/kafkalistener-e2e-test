package com.findinpath.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class to be used in productive scenarios for specifying the addresses of the
 * productive Apache Kafka ecosystem artifacts.
 */
@Configuration
@Profile("!test")
public class KafkaEcosystemPropertiesConfiguration {

	@Bean
	public KafkaEcosystemProperties kafkaEcosystemProperties(
			@Value("${kafka.bootstrapServers}") String bootstrapServers,
			@Value("${kafka.schemaRegistryUrl}") String schemaRegistryUrl) {
		return new KafkaEcosystemProperties(bootstrapServers, schemaRegistryUrl);
	}
}
