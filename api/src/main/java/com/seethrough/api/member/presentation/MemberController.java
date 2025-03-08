package com.seethrough.api.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seethrough.api.common.exception.ErrorResponse;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.member.application.service.MemberService;
import com.seethrough.api.member.presentation.dto.response.MemberListResponse;
import com.seethrough.api.member.presentation.dto.response.MemberResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(name = "구성원 관리", description = "구성원 정보를 관리하는 API")
public class MemberController {

	private final MemberService memberService;

	@GetMapping
	@Operation(
		summary = "구성원 목록 조회",
		description = "탏퇴하지 않은 모든 사용자의 목록을 페이지네이션을 적용하여 반환합니다.<br>" +
			"기본적으로 생성일 기준 내림차순으로 정렬되며, 페이지당 최대 10명의 구성원 정보를 제공합니다.<br>" +
			"정렬 기준과 방향을 변경할 수 있으며, 추가 페이지 존재 여부를 함께 반환합니다."
	)
	public ResponseEntity<SliceResponseDto<MemberListResponse>> getMemberList(
		@Parameter(description = "조회할 페이지 번호 (1부터 시작)")
		@RequestParam(defaultValue = "1") Integer page,

		@Parameter(description = "페이지당 항목 수 (최대 100)")
		@RequestParam(defaultValue = "10") Integer size,

		@Parameter(description = "정렬 기준 필드 (createdAt, name, isRegistered, recognitionTimes 등)")
		@RequestParam(defaultValue = "createdAt") String sortBy,

		@Parameter(description = "정렬 방향 (ASC: 오름차순, DESC: 내림차순)")
		@RequestParam(defaultValue = "DESC") String sortDirection
	) {
		log.info("[Controller - GET /api/member] 구성원 목록 조회 요청: page={}, size={}, sortBy={}, sortDirection={}", page, size, sortBy, sortDirection);

		SliceResponseDto<MemberListResponse> responseList = memberService.getMemberList(page, size, sortBy, sortDirection);

		if (!responseList.getContent().isEmpty()) {
			log.debug("[Controller] 첫 번째 응답 상세 정보:{}", responseList.getContent().get(0));
		}

		log.info("[Controller] 구성원 목록 조회 응답: 총 {}개 항목, 현재 페이지: {}, 마지막 페이지 여부: {}",
			responseList.getContent().size(),
			responseList.getSliceInfo().getCurrentPage(),
			responseList.getSliceInfo().getHasNext());

		return ResponseEntity.ok(responseList);
	}

	@GetMapping("/{memberId}")
	@Operation(
		summary = "구성원 조회",
		description = "UUID로 작성된 구성원의 키를 활용해 시스템에 등록된 특정 사용자를 반환합니다.<br>" +
			"해당 ID에 매칭되는 구성원이 없거나 탈퇴한 구성원인 경우 MemberNotFoundException이 발생합니다.<br>" +
			"응답으로는 구성원의 기본 정보(ID, 이름, 등록 상태 등)가 포함됩니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "구성원 조회 성공"),
		@ApiResponse(responseCode = "404", description = "구성원을 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MemberResponse> getMember(@PathVariable String memberId) {
		log.info("[Controller - GET /api/member/{memberId}] 구성원 조회 요청: memberId={}", memberId);

		MemberResponse response = memberService.getMember(memberId);

		log.debug("[Controller] 구성원 조회 응답: {}", response);

		return ResponseEntity.ok(response);
	}
}
