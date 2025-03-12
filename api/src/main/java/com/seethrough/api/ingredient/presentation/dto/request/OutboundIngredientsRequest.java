package com.seethrough.api.ingredient.presentation.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class OutboundIngredientsRequest {
	private String memberId;
	private List<String> ingredientIdList;
}
