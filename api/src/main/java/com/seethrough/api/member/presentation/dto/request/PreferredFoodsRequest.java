package com.seethrough.api.member.presentation.dto.request;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class PreferredFoodsRequest {
	private Set<String> preferredFoods;
}
