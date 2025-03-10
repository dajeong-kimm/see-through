package com.seethrough.api.member.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DetailMemberResponse {
	private String memberId;
	private String name;
	private Integer age;
	private String imagePath;
	private List<String> preferredFoods;
	private List<String> dislikedFoods;
	private Boolean isRegistered;
	private LocalDateTime createdAt;
}
