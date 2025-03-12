package com.seethrough.api.ingredient.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class IngredientFactory {

	public static Ingredient create(
		UUID memberId,
		String name,
		String imagePath,
		LocalDateTime inboundAt,
		LocalDateTime expirationAt
	) {
		return Ingredient.builder()
			.ingredientId(UUID.randomUUID())
			.memberId(memberId)
			.name(name)
			.imagePath(imagePath)
			.inboundAt(inboundAt)
			.expirationAt(expirationAt)
			.build();
	}
}
