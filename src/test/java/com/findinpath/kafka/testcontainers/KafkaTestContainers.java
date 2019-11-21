package com.findinpath.kafka.testcontainers;

import java.io.IOException;
import java.util.stream.Stream;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;

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

    Startables
        .deepStart(Stream.of(zookeeperContainer, kafkaContainer, schemaRegistryContainer))
        .join();
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