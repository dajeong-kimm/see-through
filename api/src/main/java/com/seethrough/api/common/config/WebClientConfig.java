package com.seethrough.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Value("${nickname.url}")
	private String nicknameUrl;

	@Bean
	public WebClient nicknameWebClient(WebClient.Builder builder) {
		return builder.baseUrl(nicknameUrl)
			.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
			.build();
	}
}
