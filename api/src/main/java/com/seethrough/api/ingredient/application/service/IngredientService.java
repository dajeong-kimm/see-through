package com.seethrough.api.ingredient.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.infrastructure.llm.LlmApiService;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmInboundIngredientsRequest;
import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.ingredient.application.mapper.IngredientDtoMapper;
import com.seethrough.api.ingredient.domain.Ingredient;
import com.seethrough.api.ingredient.domain.IngredientLog;
import com.seethrough.api.ingredient.domain.IngredientLogRepository;
import com.seethrough.api.ingredient.domain.IngredientRepository;
import com.seethrough.api.ingredient.domain.MovementType;
import com.seethrough.api.ingredient.exception.IngredientNotFoundException;
import com.seethrough.api.ingredient.presentation.dto.request.InboundIngredientsRequest;
import com.seethrough.api.ingredient.presentation.dto.response.IngredientDetailResponse;
import com.seethrough.api.ingredient.presentation.dto.response.IngredientListResponse;
import com.seethrough.api.member.application.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IngredientService {

	private final IngredientRepository ingredientRepository;
	private final IngredientDtoMapper ingredientDtoMapper;
	private final IngredientLogRepository ingredientLogRepository;
	private final MemberService memberService;
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

	public IngredientDetailResponse getIngredientDetail(String ingredientId) {
		log.debug("[Service] getIngredientDetail 호출");

		UUID ingredientIdObj = UUID.fromString(ingredientId);

		Ingredient ingredient = findIngredient(ingredientIdObj);

		return ingredientDtoMapper.toDetailResponse(ingredient);
	}

	private Ingredient findIngredient(UUID ingredientId) {
		return ingredientRepository.findByIngredientId(ingredientId)
			.orElseThrow(() ->
				new IngredientNotFoundException("식재료를 찾을 수 없습니다.")
			);
	}

	@Transactional
	public void inboundIngredients(InboundIngredientsRequest request) {
		log.debug("[Service] inboundIngredients 호출");

		UUID memberIdObj = UUID.fromString(request.getMemberId());
		memberService.checkMemberExists(memberIdObj);

		LocalDateTime now = LocalDateTime.now();

		List<Ingredient> ingredients = ingredientDtoMapper.toIngredientList(
			memberIdObj, request.getInboundIngredientRequestList(), now
		);
		ingredientRepository.saveAll(ingredients);

		List<IngredientLog> ingredientLogs = ingredientDtoMapper.toIngredientLogList(
			memberIdObj, request.getInboundIngredientRequestList(), MovementType.INBOUND, now
		);
		ingredientLogRepository.saveAll(ingredientLogs);

		LlmInboundIngredientsRequest llmRequest = LlmInboundIngredientsRequest.from(ingredients);
		llmApiService.sendIngredientsInbound(llmRequest);
	}
}
