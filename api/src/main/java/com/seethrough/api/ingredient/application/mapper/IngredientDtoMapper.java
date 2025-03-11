package com.seethrough.api.ingredient.application.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.seethrough.api.ingredient.domain.Ingredient;
import com.seethrough.api.ingredient.domain.IngredientLog;
import com.seethrough.api.ingredient.domain.MovementType;
import com.seethrough.api.ingredient.presentation.dto.request.InboundIngredientRequest;
import com.seethrough.api.ingredient.presentation.dto.response.IngredientDetailResponse;
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

	public IngredientDetailResponse toDetailResponse(Ingredient ingredient) {
		return IngredientDetailResponse.builder()
			.ingredientId(ingredient.getIngredientId().toString())
			.name(ingredient.getName())
			.imagePath(ingredient.getImagePath())
			.memberId(ingredient.getMember().getMemberId().toString())
			.memberName(ingredient.getMember().getName())
			.memberImagePath(ingredient.getMember().getImagePath())
			.inboundAt(ingredient.getInboundAt())
			.expirationAt(ingredient.getExpirationAt())
			.build();
	}

	public List<Ingredient> toIngredientList(UUID memberId, List<InboundIngredientRequest> listRequest) {
		LocalDateTime now = LocalDateTime.now();

		return listRequest.stream()
			.map(request ->
				toIngredient(
					UUID.randomUUID(),
					memberId,
					request.getName(),
					request.getImagePath(),
					now,
					request.getExpirationAt()
				)
			)
			.toList();
	}

	public List<IngredientLog> toIngredientLogList(UUID memberId, List<InboundIngredientRequest> listRequest, MovementType movementType) {
		LocalDateTime now = LocalDateTime.now();

		return listRequest.stream()
			.map(request ->
				toIngredientLog(
					memberId,
					request.getName(),
					movementType,
					now
				)
			)
			.toList();
	}

	private Ingredient toIngredient(
		UUID ingredientId,
		UUID memberId,
		String name,
		String imagePath,
		LocalDateTime inboundAt,
		LocalDateTime expirationAt
	) {
		return Ingredient.builder()
			.ingredientId(ingredientId)
			.memberId(memberId)
			.name(name)
			.imagePath(imagePath)
			.inboundAt(inboundAt)
			.expirationAt(expirationAt)
			.build();
	}

	private IngredientLog toIngredientLog(
		UUID memberId,
		String name,
		MovementType movementType,
		LocalDateTime createdAt
	) {
		return IngredientLog.builder()
			.memberId(memberId)
			.ingredientName(name)
			.movementType(movementType)
			.createdAt(createdAt)
			.build();
	}
}
