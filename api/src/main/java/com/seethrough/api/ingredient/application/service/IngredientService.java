package com.seethrough.api.ingredient.application.service;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.infrastructure.llm.LlmApiService;
import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.ingredient.application.mapper.IngredientDtoMapper;
import com.seethrough.api.ingredient.domain.Ingredient;
import com.seethrough.api.ingredient.domain.IngredientRepository;
import com.seethrough.api.ingredient.presentation.dto.response.IngredientListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IngredientService {

	private final IngredientRepository ingredientRepository;
	private final IngredientDtoMapper ingredientDtoMapper;
	private final LlmApiService llmApiService;

	public SliceResponseDto<IngredientListResponse> getIngredientList(
		Integer page, Integer size, String sortBy, String sortDirection
	) {
		log.debug("[Service] getIngredientList 호출");

		SliceRequestDto sliceRequestDto = SliceRequestDto.builder()
			.page(page)
			.size(size)
			.sortBy(sortBy)
			.sortDirection(sortDirection)
			.build();

		Slice<Ingredient> ingredients = ingredientRepository.findIngredients(sliceRequestDto.toPageable());

		return SliceResponseDto.of(ingredients.map(ingredientDtoMapper::toListResponse));
	}
}
