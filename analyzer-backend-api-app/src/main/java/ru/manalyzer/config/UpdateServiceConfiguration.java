package ru.manalyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "update.service")
public class UpdateServiceConfiguration {
    private long initialDelay;
    private long delayBetweenUpdate;
    private String timeUnit;
}
