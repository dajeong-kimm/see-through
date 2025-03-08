package com.seethrough.api.member.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.seethrough.api.common.value.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Member {
	private UUID memberId;
	private String name;
	private Integer age;
	private String imagePath;
	private List<String> preferredFoods;
	private List<String> dislikedFoods;
	private Boolean isRegistered;
	private Integer recognitionTimes;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	// TODO: 검증 로직
}
