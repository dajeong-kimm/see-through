package com.seethrough.api.ingredient.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.infrastructure.llm.LlmApiService;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmInboundIngredientsRequest;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmPersonalNoticeRequest;
import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.ingredient.application.mapper.IngredientDtoMapper;
import com.seethrough.api.ingredient.domain.Ingredient;
import com.seethrough.api.ingredient.domain.IngredientFactory;
import com.seethrough.api.ingredient.domain.IngredientLog;
import com.seethrough.api.ingredient.domain.IngredientLogFactory;
import com.seethrough.api.ingredient.domain.IngredientLogRepository;
import com.seethrough.api.ingredient.domain.IngredientRepository;
import com.seethrough.api.ingredient.domain.MovementType;
import com.seethrough.api.ingredient.exception.IngredientNotFoundException;
import com.seethrough.api.ingredient.presentation.dto.request.InboundIngredientsRequest;
import com.seethrough.api.ingredient.presentation.dto.request.OutboundIngredientsRequest;
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

		List<Ingredient> ingredients = request.getInboundIngredientRequestList()
			.stream()
			.map(obj -> IngredientFactory.create(memberIdObj, obj.getName(), obj.getImagePath(), now, obj.getExpirationAt()))
			.toList();
		ingredientRepository.saveAll(ingredients);

		List<IngredientLog> ingredientLogs = IngredientLogFactory.createList(memberIdObj, ingredients, MovementType.INBOUND, now);
		ingredientLogRepository.saveAll(ingredientLogs);

		LlmInboundIngredientsRequest llmRequest = LlmInboundIngredientsRequest.from(ingredients);
		llmApiService.sendIngredientsInbound(llmRequest);
	}

	@Transactional
	public CompletableFuture<String> outboundIngredients(OutboundIngredientsRequest request) {
		log.debug("[Service] outboundIngredients 호출");

		UUID memberIdObj = UUID.fromString(request.getMemberId());
		memberService.checkMemberExists(memberIdObj);

		// TODO: UUID 유틸로 뽑아내기
		List<UUID> ingredientIdList = request.getIngredientIdList()
			.stream()
			.map(UUID::fromString)
			.toList();

		LocalDateTime now = LocalDateTime.now();

		List<Ingredient> ingredients = ingredientRepository.findIngredientsByIngredientId(ingredientIdList);
		ingredientRepository.deleteAll(ingredients);

		System.out.println(ingredients.size() + "asdfjklas;dfjasld");

		List<IngredientLog> ingredientLogs = IngredientLogFactory.createList(memberIdObj, ingredients, MovementType.OUTBOUND, now);
		ingredientLogRepository.saveAll(ingredientLogs);

		System.out.println(ingredients.size());
		if (ingredients.size() == 1) {
			LlmPersonalNoticeRequest llmRequest = LlmPersonalNoticeRequest.from(memberIdObj, ingredients.get(0).getName(), now);
			return llmApiService.sendPersonalNotice(llmRequest);
		}

		return null;
	}
}
