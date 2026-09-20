package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Getter
@Setter
@Configuration
public class OpenRouterConfig {

    @Value("${openrouter.base-url}")
    private String baseUrl;

    @Value("${openrouter.api-key:${openrouter.api.key}}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.timeout:30}")
    private int timeout;

    @Bean
    public RestClient openRouterRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "https://github.com/hiswaraj/ai-text-classification-api")
                .defaultHeader("X-Title", "AI Text Classification API")
                .build();
    }
}
