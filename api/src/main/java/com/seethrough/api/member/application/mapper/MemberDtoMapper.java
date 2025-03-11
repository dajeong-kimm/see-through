package com.seethrough.api.member.application.mapper;

import org.springframework.stereotype.Component;

import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.presentation.dto.response.DetailMemberResponse;
import com.seethrough.api.member.presentation.dto.response.MemberListResponse;

@Component
public class MemberDtoMapper {

	public MemberListResponse toListResponse(Member member) {
		return MemberListResponse.builder()
			.memberId(member.getMemberId().toString())
			.name(member.getName())
			.imagePath(member.getImagePath())
			.isRegistered(member.getIsRegistered())
			.build();
	}

	public DetailMemberResponse toResponse(Member member) {
		return DetailMemberResponse.builder()
			.memberId(member.getMemberId().toString())
			.name(member.getName())
			.age(member.getAge())
			.imagePath(member.getImagePath())
			.preferredFoods(member.getPreferredFoods())
			.dislikedFoods(member.getDislikedFoods())
			.isRegistered(member.getIsRegistered())
			.createdAt(member.getCreatedAt())
			.build();
	}
}
