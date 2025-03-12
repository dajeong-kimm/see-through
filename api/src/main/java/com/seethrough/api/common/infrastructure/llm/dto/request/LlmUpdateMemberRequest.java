package com.seethrough.api.common.infrastructure.llm.dto.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty("member_id")
	private String memberId;

	@JsonProperty("age")
	private int age;

	@JsonProperty("preferred_foods")
	private Set<String> preferredFoods;

	@JsonProperty("disliked_foods")
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
