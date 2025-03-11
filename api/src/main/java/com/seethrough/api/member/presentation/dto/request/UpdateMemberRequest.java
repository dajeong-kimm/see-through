package com.seethrough.api.member.presentation.dto.request;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UpdateMemberRequest {
	private String memberId;
	private String name;
	private int age;
	private Set<String> preferredFoods;
	private Set<String> dislikedFoods;
}
