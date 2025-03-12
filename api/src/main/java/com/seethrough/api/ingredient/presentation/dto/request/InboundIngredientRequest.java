package com.seethrough.api.ingredient.presentation.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class InboundIngredientRequest {
	private String name;
	private String imagePath;
	private LocalDateTime expirationAt;
}
