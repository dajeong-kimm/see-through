package com.seethrough.api.ingredient.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class IngredientLogFactory {

	public static List<IngredientLog> createList(UUID memberId, List<Ingredient> ingredients, MovementType movementType, LocalDateTime createdAt) {
		return ingredients.stream()
			.map(ingredient -> create(memberId, ingredient.getName(), movementType, createdAt))
			.toList();
	}

	public static IngredientLog create(
		UUID memberId,
		String ingredientName,
		MovementType movementType,
		LocalDateTime createdAt
	) {
		return IngredientLog.builder()
			.memberId(memberId)
			.ingredientName(ingredientName)
			.movementType(movementType)
			.createdAt(createdAt)
			.build();
	}
}
