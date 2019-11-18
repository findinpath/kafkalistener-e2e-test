package com.findinpath.kafka.testcontainers;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

public class KafkaTestContainers {

	private final ZookeeperContainer zookeeperContainer;
	private final KafkaContainer kafkaContainer;
	private final SchemaRegistryContainer schemaRegistryContainer;
	private final Network network;

	public KafkaTestContainers() throws IOException {
		this.network = Network.newNetwork();
		this.zookeeperContainer = new ZookeeperContainer()
				.withNetwork(network);
		this.kafkaContainer = new KafkaContainer(zookeeperContainer.getZookeeperConnect())
				.withNetwork(network);
		this.schemaRegistryContainer = new SchemaRegistryContainer(
				zookeeperContainer.getZookeeperConnect())
				.withNetwork(network);

		Runtime.getRuntime()
				.addShutdownHook(new Thread(() ->
								Arrays.asList(zookeeperContainer, kafkaContainer, schemaRegistryContainer)
										.parallelStream().forEach(GenericContainer::stop)
						)
				);

		Stream.of(zookeeperContainer, kafkaContainer, schemaRegistryContainer).parallel()
				.forEach(GenericContainer::start);
	}

	public ZookeeperContainer getZookeeperContainer() {
		return zookeeperContainer;
	}

	public KafkaContainer getKafkaContainer() {
		return kafkaContainer;
	}

	public SchemaRegistryContainer getSchemaRegistryContainer() {
		return schemaRegistryContainer;
	}
}