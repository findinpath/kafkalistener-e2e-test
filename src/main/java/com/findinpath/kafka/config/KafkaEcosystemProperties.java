package com.findinpath.kafka.config;

/**
 * This class groups the properties related to Apache Kafka ecosystem in order to be able to use
 * them both productive (against a real Apache Kafka ecosystem) or in testing (against throwaway
 * Apache Kafka ecosystem docker container artifacts).
 */
public class KafkaEcosystemProperties {

	private final String bootstrapServers;
	private final String schemaRegistryUrl;

	public KafkaEcosystemProperties(String bootstrapServers, String schemaRegistryUrl) {
		this.bootstrapServers = bootstrapServers;
		this.schemaRegistryUrl = schemaRegistryUrl;
	}

	public String getBootstrapServers() {
		return bootstrapServers;
	}

	public String getSchemaRegistryUrl() {
		return schemaRegistryUrl;
	}

}
