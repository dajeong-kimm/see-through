package com.seethrough.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {

	@Value("${llm.url}")
	private String llmUrl;

	@Value("${nickname.url}")
	private String nicknameUrl;

	@Bean
	public WebClient llmWebClient(WebClient.Builder builder) {
		return builder.baseUrl(llmUrl)
			.filter((request, next) -> {
				log.info("[LlmWebClient] URL: {}", request.url());
				return next.exchange(request);
			})
			.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
			.build();
	}

	@Bean
	public WebClient nicknameWebClient(WebClient.Builder builder) {
		return builder.baseUrl(nicknameUrl)
			.filter((request, next) -> {
				log.info("[NicknameWebClient] URL: {}", request.url());
				return next.exchange(request);
			})
			.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
			.build();
	}
}
