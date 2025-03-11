package com.seethrough.api.member.application.service;

import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seethrough.api.common.infrastructure.llm.LlmApiService;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmUpdateMemberRequest;
import com.seethrough.api.common.pagination.SliceRequestDto;
import com.seethrough.api.common.pagination.SliceResponseDto;
import com.seethrough.api.member.application.dto.LoginMemberResult;
import com.seethrough.api.member.application.mapper.MemberDtoMapper;
import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.domain.MemberRepository;
import com.seethrough.api.member.exception.MemberNotFoundException;
import com.seethrough.api.member.infrastructure.nickname.NicknameApiService;
import com.seethrough.api.member.presentation.dto.request.DislikedFoodsRequest;
import com.seethrough.api.member.presentation.dto.request.LoginMemberRequest;
import com.seethrough.api.member.presentation.dto.request.PreferredFoodsRequest;
import com.seethrough.api.member.presentation.dto.request.UpdateMemberRequest;
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
	private final LlmApiService llmApiService;
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
	public Boolean updateMember(UpdateMemberRequest request) {
		log.debug("[Service] updateMember 호출");

		UUID memberIdObj = UUID.fromString(request.getMemberId());

		Member member = findMember(memberIdObj);

		member.update(
			request.getName(),
			request.getAge(),
			request.getPreferredFoods(),
			request.getDislikedFoods()
		);

		updateLlmMember(member);

		return true;
	}

	@Transactional
	public Boolean deleteMember(String memberId) {
		log.debug("[Service] deleteMember 호출");

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		member.delete();

		return true;
	}

	@Transactional
	public Boolean addPreferredFoods(String memberId, PreferredFoodsRequest request) {
		log.debug("[Service] addPreferredFoods 호출");

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		member.addPreferredFoods(request.getPreferredFoods());

		updateLlmMember(member);

		return true;
	}

	@Transactional
	public Boolean removePreferredFoods(String memberId, PreferredFoodsRequest request) {
		log.debug("[Service] removePreferredFoods 호출");

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		member.removePreferredFoods(request.getPreferredFoods());

		updateLlmMember(member);

		return true;
	}

	@Transactional
	public Boolean addDislikedFoods(String memberId, DislikedFoodsRequest request) {
		log.debug("[Service] addDislikedFoods 호출");

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		member.addDislikedFoods(request.getDislikedFoods());

		updateLlmMember(member);

		return true;
	}

	@Transactional
	public Boolean removeDislikedFoods(String memberId, DislikedFoodsRequest request) {
		log.debug("[Service] removeDislikedFoods 호출");

		UUID memberIdObj = UUID.fromString(memberId);

		Member member = findMember(memberIdObj);

		member.removeDislikedFoods(request.getDislikedFoods());

		updateLlmMember(member);

		return true;
	}

	private Member findMember(UUID memberId) {
		return memberRepository.findByMemberId(memberId)
			.orElseThrow(() ->
				new MemberNotFoundException("구성원을 찾을 수 없습니다.")
			);
	}

	private void updateLlmMember(Member member) {
		LlmUpdateMemberRequest llmRequest = LlmUpdateMemberRequest.from(member);
		llmApiService.sendMemberUpdate(llmRequest);
	}
}
