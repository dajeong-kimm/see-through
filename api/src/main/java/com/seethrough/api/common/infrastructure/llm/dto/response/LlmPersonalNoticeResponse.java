package com.seethrough.api.common.infrastructure.llm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LlmPersonalNoticeResponse {
	@JsonProperty("notice_message")
	private String noticeMessage;
}
