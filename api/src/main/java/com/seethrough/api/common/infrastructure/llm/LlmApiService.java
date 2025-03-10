package com.seethrough.api.common.infrastructure.llm;

import org.springframework.stereotype.Service;

import com.seethrough.api.common.infrastructure.TransactionCallbackManager;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmInboundIngredientsRequest;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmUpdateMemberRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmApiService {

	private final LlmApiClient llmApiClient;
	private final TransactionCallbackManager transactionCallbackManager;

	public void sendMemberUpdate(LlmUpdateMemberRequest request) {
		transactionCallbackManager.executeAfterCommit(() -> {
			log.info("[LlmApiService] 외부 API 구성원 갱신 요청 시작");

			llmApiClient.sendRequest("/llm/upcreate_at_user", request)
				.subscribe(
					success -> log.info("[LlmApiService] 외부 API 구성원 업데이트 성공: memberId={}", request.getMemberId()),
					error -> log.error("[LlmApiService] 외부 API 구성원 업데이트 실패 (최대 재시도 후): memberId={}, error={}",
						request.getMemberId(), error.getMessage())
				);
		});
	}

	public void sendIngredientsInbound(LlmInboundIngredientsRequest request) {
		transactionCallbackManager.executeAfterCommit(() -> {
			log.info("[LlmApiService] 외부 API 식재료 입고 요청 시작");

			llmApiClient.sendRequest("/llm/upcreate_at_inventory", request)
				.subscribe(
					success -> log.info("[LlmApiService] 외부 API 식재료 입고 성공"),
					error -> log.error("[LlmApiService] 외부 API 식재료 입고 실패 (최대 재시도 후): error={}", error.getMessage())
				);
		});
	}
}
