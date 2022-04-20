package ru.manalyzer.parser.mvideo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {
    @Bean
    public WebClient webClient(MVideoProperties.Connection connection) {
        return WebClient.builder()
                .baseUrl(connection.getBaseUrl())
                .build();
    }
}
