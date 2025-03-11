package com.seethrough.api.common.infrastructure.llm;

import org.springframework.stereotype.Service;

import com.seethrough.api.common.infrastructure.TransactionCallbackManager;
import com.seethrough.api.common.infrastructure.llm.dto.request.LlmUpdateMemberRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmApiService {

	private final LlmApiClient llmApiClient;
	private final TransactionCallbackManager transactionCallbackManager;

	/**
	 * 트랜잭션 커밋 후 구성원 업데이트 정보를 외부 API로 전송합니다.
	 * 최대 3회 재시도하며, 결과는 로그로만 기록됩니다.
	 */
	public void sendMemberUpdate(LlmUpdateMemberRequest request) {
		transactionCallbackManager.executeAfterCommit(() -> {
			log.info("[LlmApiService] 외부 API 구성원 갱신 요청 시작");

			llmApiClient.updateMember(request)
				.subscribe(
					success -> log.info("[LlmApiService] 외부 API 구성원 업데이트 성공: memberId={}", request.getMemberId()),
					error -> log.error("[LlmApiService] 외부 API 구성원 업데이트 실패 (최대 재시도 후): memberId={}, error={}",
						request.getMemberId(), error.getMessage())
				);
		});
	}
}
