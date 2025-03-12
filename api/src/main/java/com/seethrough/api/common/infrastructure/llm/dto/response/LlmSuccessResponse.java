package com.seethrough.api.common.infrastructure.llm.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LlmSuccessResponse {
	private String message;
}
