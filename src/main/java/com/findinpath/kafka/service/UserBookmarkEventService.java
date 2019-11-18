package com.findinpath.kafka.service;

import com.findinpath.kafka.dto.UserBookmarkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This service simply logs the content Json/Avro UserBookmarkEvent objects that it ingests.
 */
@Service
public class UserBookmarkEventService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserBookmarkEventService.class);


	public void ingest(UserBookmarkEvent userBookmarkEvent) {
		LOGGER.info("Handling the ingestion of the json userBookmarkEvent: " + userBookmarkEvent);
	}

	public void ingest(com.findinpath.kafka.dto.avro.UserBookmarkEvent userBookmarkEvent) {
		LOGGER.info("Handling the ingestion of the avro userBookmarkEvent: " + userBookmarkEvent);
	}
}
