package com.seethrough.api.member.application.service;

import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.member.application.dto.LoginMemberResult;
import com.seethrough.api.member.application.mapper.MemberDtoMapper;
import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.domain.MemberRepository;
import com.seethrough.api.member.exception.MemberNotFoundException;
import com.seethrough.api.member.infrastructure.external.nickname.NicknameApiService;
import com.seethrough.api.member.presentation.dto.request.LoginMemberRequest;
import com.seethrough.api.member.presentation.dto.response.DetailMemberResponse;
import com.seethrough.api.member.presentation.dto.response.MemberListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberDtoMapper memberDtoMapper;
	private final NicknameApiService nicknameApiService;

	@Transactional
	public LoginMemberResult login(LoginMemberRequest request) {
		log.debug("[Service] login 호출");

		UUID memberIdObj = UUID.fromString(request.getMemberId());

		boolean isNewMember = false;
		Member member;

		try {
			member = findMember(memberIdObj);

			log.info("[Service] 기존 구성원 식별");

			member.login(request.getAge(), request.getImagePath());
		} catch (MemberNotFoundException e) {
			log.info("[Service] 신규 구성원 생성");

			isNewMember = true;

			Member.MemberBuilder newMemberBuilder = Member.builder()
				.memberId(UUID.fromString(request.getMemberId()))
				.age(request.getAge())
				.imagePath(request.getImagePath())
				.recognitionTimes(1);

			String name = nicknameApiService.getNicknameSync();

			if (name != null) {
				log.debug("[Service] 랜덤 닉네임 생성 완료: {}", name);
				newMemberBuilder.name(name);
			}

			member = newMemberBuilder.build();

			memberRepository.save(member);
		}

		return LoginMemberResult.builder()
			.isNewMember(isNewMember)
			.response(memberDtoMapper.toResponse(member))
			.build();
	}

	public SliceResponseDto<MemberListResponse> getMemberList(
		Integer page, Integer size, String sortBy, String sortDirection
	) {
		log.debug("[Service] getMemberList 호출");

		SliceRequestDto sliceRequestDto = SliceRequestDto.builder()
			.page(page)
			.size(size)
			.sortBy(sortBy)
			.sortDirection(sortDirection)
			.build();

		Slice<Member> members = memberRepository.findMembers(sliceRequestDto.toPageable());

		return SliceResponseDto.of(members.map(memberDtoMapper::toListResponse));
	}

	public DetailMemberResponse getMemberDetail(String memberId) {
		log.debug("[Service] getMemberDetail 호출");

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		return memberDtoMapper.toResponse(member);
	}

	@Transactional
	public Boolean deleteMember(String memberId) {
		log.debug("[Service] deleteMember 호출");

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
