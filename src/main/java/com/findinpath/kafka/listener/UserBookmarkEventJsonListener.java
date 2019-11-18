package com.findinpath.kafka.listener;

import com.findinpath.kafka.dto.UserBookmarkEvent;
import com.findinpath.kafka.service.UserBookmarkEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Listener class for JSON `UserBookmarkEvent` events.
 *
 * It simply delegates the responsibility of handling the incoming events to the {@link
 * UserBookmarkEventService}.
 */
@Service
public class UserBookmarkEventJsonListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserBookmarkEventJsonListener.class);

	private final UserBookmarkEventService userBookmarkEventService;

	public UserBookmarkEventJsonListener(UserBookmarkEventService userBookmarkEventService) {
		this.userBookmarkEventService = userBookmarkEventService;
	}

	@KafkaListener(topics = "#{'${kafka.userBookmarkEventsJson.topic}'.split(',')}",
			containerFactory = "userBookmarkEventJsonKafkaListenerContainerFactory")
	public void consumeUserBookmarkEvent(UserBookmarkEvent userBookmarkEvent) {
		LOGGER.info("Consuming event " + userBookmarkEvent);
		userBookmarkEventService.ingest(userBookmarkEvent);
	}
}
