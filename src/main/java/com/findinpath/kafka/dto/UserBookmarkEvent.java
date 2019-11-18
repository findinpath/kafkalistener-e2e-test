package com.findinpath.kafka.dto;

import java.util.Objects;

public class UserBookmarkEvent {

	private String userId;
	private String url;
	private long timestamp;

	public UserBookmarkEvent() {
	}

	public UserBookmarkEvent(String userId, String url, long timestamp) {
		this.userId = userId;
		this.url = url;
		this.timestamp = timestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserBookmarkEvent that = (UserBookmarkEvent) o;
		return timestamp == that.timestamp &&
				Objects.equals(userId, that.userId) &&
				Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, url, timestamp);
	}

	@Override
	public String toString() {
		return "UserBookmarkEvent{" +
				"userId='" + userId + '\'' +
				", url='" + url + '\'' +
				", timestamp=" + timestamp +
				'}';
	}
}
