package com.seethrough.api.member.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.seethrough.api.common.value.UUID;
import com.seethrough.api.member.domain.Member;
import com.seethrough.api.member.infrastructure.entity.MemberEntity;

@Component
public class MemberEntityMapper {

	public MemberEntity toEntity(Member member) {
		return MemberEntity.builder()
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

	public Member toDomain(MemberEntity entity) {
		return Member.builder()
			.memberId(new UUID(entity.getMemberId()))
			.name(entity.getName())
			.age(entity.getAge())
			.imagePath(entity.getImagePath())
			.preferredFoods(entity.getPreferredFoods())
			.dislikedFoods(entity.getDislikedFoods())
			.isRegistered(entity.getIsRegistered())
			.recognitionTimes(entity.getRecognitionTimes())
			.createdAt(entity.getCreatedAt())
			.deletedAt(entity.getDeletedAt())
			.build();
	}
}
