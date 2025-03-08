package com.seethrough.api.member.application.mapper;

import org.springframework.stereotype.Component;

import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.presentation.dto.response.MemberResponse;

@Component
public class MemberDtoMapper {

	public MemberResponse toResponse(Member member) {
		return MemberResponse.builder()
			.memberId(member.getMemberId().value())
			.name(member.getName())
			.age(member.getAge())
			.imagePath(member.getImagePath())
			.preferredFoods(member.getPreferredFoods())
			.dislikedFoods(member.getDislikedFoods())
			.isRegistered(member.getIsRegistered())
			.recognitionTimes(member.getRecognitionTimes())
			.createdAt(member.getCreatedAt())
			.deletedAt(member.getDeletedAt())
			.build();
	}
}
