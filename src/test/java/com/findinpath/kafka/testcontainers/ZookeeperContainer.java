package com.findinpath.kafka.testcontainers;

import java.io.IOException;
import java.util.HashMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

import static com.findinpath.kafka.testcontainers.Utils.CONFLUENT_PLATFORM_VERSION;
import static com.findinpath.kafka.testcontainers.Utils.getRandomFreePort;

public class ZookeeperContainer extends GenericContainer<ZookeeperContainer> {

	private static final int ZOOKEEPER_INTERNAL_PORT = 2181;
	private static final int ZOOKEEPER_TICK_TIME = 2000;

	private final int exposedPort;
	private final String networkAlias = "zookeeper";
	private final String zookeeperConnect;

	public ZookeeperContainer() throws IOException {
		this(CONFLUENT_PLATFORM_VERSION);
	}

	public ZookeeperContainer(String confluentPlatformVersion) throws IOException {
		super(getZookeeperContainerImage(confluentPlatformVersion));
		this.exposedPort = getRandomFreePort();

		var env = new HashMap<String, String>();
		env.put("ZOOKEEPER_CLIENT_PORT", Integer.toString(ZOOKEEPER_INTERNAL_PORT));
		env.put("ZOOKEEPER_TICK_TIME", Integer.toString(ZOOKEEPER_TICK_TIME));
		withEnv(env);

		addFixedExposedPort(exposedPort, ZOOKEEPER_INTERNAL_PORT);
		withNetworkAliases(networkAlias);
		this.zookeeperConnect = networkAlias + ":" + ZOOKEEPER_INTERNAL_PORT;
	}

	private static String getZookeeperContainerImage(String confluentPlatformVersion) {
		return (String) TestcontainersConfiguration
				.getInstance().getProperties().getOrDefault(
						"zookeeper.container.image",
						"confluentinc/cp-zookeeper:" + confluentPlatformVersion
				);
	}

	public String getZookeeperConnect() {
		return zookeeperConnect;
	}

	public int getExposedPort() {
		return exposedPort;
	}
}