package com.seethrough.api.ingredient.application.mapper;

import org.springframework.stereotype.Component;

import com.seethrough.api.ingredient.domain.Ingredient;
import com.seethrough.api.ingredient.presentation.dto.response.IngredientListResponse;

@Component
public class IngredientDtoMapper {

	public IngredientListResponse toListResponse(Ingredient ingredient) {
		return IngredientListResponse.builder()
			.ingredientId(ingredient.getIngredientId().toString())
			.name(ingredient.getName())
			.imagePath(ingredient.getImagePath())
			.inboundAt(ingredient.getInboundAt())
			.expirationAt(ingredient.getExpirationAt())
			.build();
	}
}
