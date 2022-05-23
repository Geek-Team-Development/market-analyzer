package ru.manalyzer.parser.mvideo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.manalyzer.parser.mvideo.config.properties.ConnectionProperties;
import ru.manalyzer.parser.mvideo.config.properties.MVideoProperties;

@Configuration
public class Config {
    @Bean
    public WebClient webClient(ConnectionProperties connection) {
        return WebClient.builder()
                .baseUrl(connection.getBaseUrl())
                .build();
    }
}
