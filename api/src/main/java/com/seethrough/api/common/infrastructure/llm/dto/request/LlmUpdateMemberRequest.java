package com.seethrough.api.common.infrastructure.llm.dto.request;

import java.util.Set;

import com.seethrough.api.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class LlmUpdateMemberRequest {
	private String memberId;
	private int age;
	private Set<String> preferredFoods;
	private Set<String> dislikedFoods;

	public static LlmUpdateMemberRequest from(Member member) {
		return LlmUpdateMemberRequest.builder()
			.memberId(member.getMemberId().toString())
			.age(member.getAge())
			.preferredFoods(member.getPreferredFoods())
			.dislikedFoods(member.getDislikedFoods())
			.build();
	}
}
