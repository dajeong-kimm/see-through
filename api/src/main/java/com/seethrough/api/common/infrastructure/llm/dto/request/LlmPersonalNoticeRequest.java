package com.seethrough.api.common.infrastructure.llm.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class LlmPersonalNoticeRequest {
	@JsonProperty("member_id")
	private String memberId;

	@JsonProperty("removed_ingredient")
	private String removedIngredient;

	@JsonProperty("date")
	private LocalDateTime date;

	public static LlmPersonalNoticeRequest from(UUID memberId, String removedIngredient, LocalDateTime date) {
		return LlmPersonalNoticeRequest.builder()
			.memberId(memberId.toString())
			.removedIngredient(removedIngredient)
			.date(date)
			.build();
	}
}
