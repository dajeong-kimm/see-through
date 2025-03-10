package com.seethrough.api.common.infrastructure.llm;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmApiClient {

	private static final int MAX_RETRY_ATTEMPTS = 3;
	private static final int TIMEOUT_SECONDS = 5;

	private final WebClient llmWebClient;

	public <T> Mono<Boolean> sendRequest(String uri, T request) {
		return llmWebClient.put()
			.uri(uri)
			.bodyValue(request)
			.retrieve()
			.bodyToMono(Boolean.class)
			.timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
			.retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, Duration.ofMillis(500))
				.doBeforeRetry(retrySignal ->
					log.warn("[LlmApiClient] 재시도 #{}: {}",
						retrySignal.totalRetries() + 1,
						retrySignal.failure().getMessage())
				)
			);
	}
}
