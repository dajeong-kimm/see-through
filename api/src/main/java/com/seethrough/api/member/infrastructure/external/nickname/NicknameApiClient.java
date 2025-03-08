package com.seethrough.api.member.infrastructure.external.nickname;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NicknameApiClient {

	private final WebClient nicknameWebClient;

	public Mono<String> getNickname() {
		return nicknameWebClient.get()
			.uri("/?format=text&count=1")
			.retrieve()
			.bodyToMono(String.class)
			.onErrorResume(e -> {
				// 오류 로깅 후 빈 Mono 반환
				return Mono.empty();
			});
	}
}
