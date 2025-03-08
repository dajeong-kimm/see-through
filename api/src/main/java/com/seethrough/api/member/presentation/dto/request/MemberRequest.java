package com.seethrough.api.member.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MemberRequest {
	private String memberId;
	private Integer age;
	private String imagePath;
}
