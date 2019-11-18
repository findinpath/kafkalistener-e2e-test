package com.findinpath.kafka.listener;

import com.findinpath.kafka.config.KafkaEcosystemProperties;
import com.findinpath.kafka.dto.avro.UserBookmarkEvent;
import com.findinpath.kafka.service.UserBookmarkEventService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * This tests checks whether the messages sent in AVRO format over the
 * `${kafka.userBookmarkEventsAvro.topic}` topic get correctly deserialized by the {@link
 * UserBookmarkEventAvroListener} and that the corresponding service is being called to handle these
 * messages.
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserBookmarkEventAvroListenerTest {

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	@Autowired
	private KafkaEcosystemProperties kafkaEcosystemProperties;

	@MockBean
	private UserBookmarkEventService userBookmarkEventService;

	@Value("${kafka.userBookmarkEventsAvro.topic}")
	private String userBookmarkEventAvroTopic;


	@BeforeEach
	void setup() {
		// wait until the partitions are assigned
		kafkaListenerEndpointRegistry.getListenerContainers().forEach(
				messageListenerContainer -> ContainerTestUtils
						.waitForAssignment(messageListenerContainer, 1)
		);
	}

	@Test
	public void demo() {
		// GIVEN
		var userId = UUID.randomUUID().toString();
		var url = "https://findinpath.com";
		UserBookmarkEvent userBookmarkEvent = new UserBookmarkEvent(userId, url,
				Instant.now().toEpochMilli());

		// WHEN
		writeToTopic(userBookmarkEventAvroTopic, userBookmarkEvent);

		// THEN
		var argumentCaptor = ArgumentCaptor.forClass(UserBookmarkEvent.class);
		verify(userBookmarkEventService, timeout(10_000)).ingest(argumentCaptor.capture());
		UserBookmarkEvent capturedUserBookmarkEvent = argumentCaptor.getValue();
		assertThat(userBookmarkEvent, equalTo(capturedUserBookmarkEvent));
	}


	private KafkaProducer<String, UserBookmarkEvent> createProducer(
			KafkaEcosystemProperties kafkaEcosystemProperties) {
		final Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				kafkaEcosystemProperties.getBootstrapServers());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				kafkaEcosystemProperties.getSchemaRegistryUrl());

		return new KafkaProducer<>(props);
	}

	private void writeToTopic(String topicName, UserBookmarkEvent... userBookmarkEvents) {

		try (KafkaProducer<String, UserBookmarkEvent> producer = createProducer(
				kafkaEcosystemProperties)) {
			Arrays.stream(userBookmarkEvents)
					.forEach(userBookmarkEvent -> {
								var record = new ProducerRecord<>(topicName, userBookmarkEvent.getUserId(),
										userBookmarkEvent);
								producer.send(record);
							}
					);

			producer.flush();
		}
	}
}