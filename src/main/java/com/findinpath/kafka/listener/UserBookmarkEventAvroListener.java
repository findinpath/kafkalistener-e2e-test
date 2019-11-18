package com.findinpath.kafka.listener;

import com.findinpath.kafka.dto.avro.UserBookmarkEvent;
import com.findinpath.kafka.service.UserBookmarkEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Listener class for AVRO `UserBookmarkEvent` events.
 *
 * It simply delegates the responsibility of handling the incoming events to the {@link
 * UserBookmarkEventService}.
 */
@Service
public class UserBookmarkEventAvroListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserBookmarkEventAvroListener.class);

	private final UserBookmarkEventService userBookmarkEventService;

	public UserBookmarkEventAvroListener(UserBookmarkEventService userBookmarkEventService) {
		this.userBookmarkEventService = userBookmarkEventService;
	}

	@KafkaListener(topics = "#{'${kafka.userBookmarkEventsAvro.topic}'.split(',')}",
			containerFactory = "userBookmarkEventAvroKafkaListenerContainerFactory")
	public void consumeUserBookmarkEvent(UserBookmarkEvent userBookmarkEvent) {
		LOGGER.info("Consuming event " + userBookmarkEvent);
		userBookmarkEventService.ingest(userBookmarkEvent);
	}

}
