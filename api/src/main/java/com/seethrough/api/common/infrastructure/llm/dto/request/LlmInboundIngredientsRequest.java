package com.seethrough.api.common.infrastructure.llm.dto.request;

import java.util.List;
import java.util.stream.Collectors;

import com.seethrough.api.ingredient.domain.Ingredient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class LlmInboundIngredientsRequest {
	private List<String> ingredients;

	public static LlmInboundIngredientsRequest from(List<Ingredient> ingredients) {
		return LlmInboundIngredientsRequest.builder()
			.ingredients(ingredients.stream()
				.map(Ingredient::getName)
				.collect(Collectors.toList())
			)
			.build();
	}
}
