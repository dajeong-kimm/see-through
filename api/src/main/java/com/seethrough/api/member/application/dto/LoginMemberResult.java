package com.seethrough.api.member.application.dto;

import com.seethrough.api.member.presentation.dto.response.DetailMemberResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LoginMemberResult {
	private boolean isNewMember;
	private DetailMemberResponse response;
}
