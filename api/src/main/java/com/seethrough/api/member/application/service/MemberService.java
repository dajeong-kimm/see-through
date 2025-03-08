package com.seethrough.api.member.application.service;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.member.application.mapper.MemberDtoMapper;
import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.domain.MemberRepository;
import com.seethrough.api.member.presentation.dto.response.MemberResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberDtoMapper memberDtoMapper;

	public SliceResponseDto<MemberResponse> getMemberList(
		Integer page, Integer size, String sortBy, String sortDirection
	) {
		log.debug("[Service] getMemberList 호출: page={}, size={}, sortBy={}, sortDirection={}", page, size, sortBy, sortDirection);

		SliceRequestDto sliceRequestDto = SliceRequestDto.builder()
			.page(page)
			.size(size)
			.sortBy(sortBy)
			.sortDirection(sortDirection)
			.build();

		Slice<Member> members = memberRepository.findMembers(sliceRequestDto.toPageable());

		if (!members.getContent().isEmpty()) {
			log.debug("[Service] 첫 번째 구성원 상세 정보:{}", members.getContent().get(0));
		}

		return SliceResponseDto.of(members.map(memberDtoMapper::toResponse));
	}
}
