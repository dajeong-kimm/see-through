package com.seethrough.api.common.value;

import com.seethrough.api.common.exception.InvalidUUIDException;

public record UUID(String value) {
	public UUID {
		validateUUID(value);
	}

	private void validateUUID(String value) {
		if (value == null || value.isEmpty()) {
			throw new InvalidUUIDException("UUID가 존재하지 않습니다.");
		}
	}

	public static UUID generateUUID() {
		return new UUID(java.util.UUID.randomUUID().toString());
	}
}

