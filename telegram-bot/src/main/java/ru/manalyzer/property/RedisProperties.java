package ru.manalyzer.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PropertySource("classpath:redis.properties")
@ConfigurationProperties(value = "redis")
public class RedisProperties {

    private String host;

    private Integer port;
}
