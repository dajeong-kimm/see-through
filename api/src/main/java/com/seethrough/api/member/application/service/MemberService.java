package com.seethrough.api.member.application.service;

import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.member.application.mapper.MemberDtoMapper;
import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.domain.MemberRepository;
import com.seethrough.api.member.exception.MemberNotFoundException;
import com.seethrough.api.member.presentation.dto.response.MemberListResponse;
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

	public SliceResponseDto<MemberListResponse> getMemberList(
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

		return SliceResponseDto.of(members.map(memberDtoMapper::toListResponse));
	}

	public MemberResponse getMemberDetail(String memberId) {
		log.debug("[Service] getMemberDetail 호출: memberId={}", memberId);

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		return memberDtoMapper.toResponse(member);
	}

	@Transactional
	public Boolean deleteMember(String memberId) {
		log.debug("[Service] deleteMember 호출: memberId={}", memberId);

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		member.delete();

		return true;
	}

	private Member findMember(UUID memberId) {
		return memberRepository.findByMemberId(memberId)
			.orElseThrow(() ->
				new MemberNotFoundException("구성원을 찾을 수 없습니다.")
			);
	}
}
