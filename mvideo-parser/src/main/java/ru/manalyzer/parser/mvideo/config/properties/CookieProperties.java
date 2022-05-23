package ru.manalyzer.parser.mvideo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.parser.mvideo.config.util.YamlPropertySourceFactory;

import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mvideo.cookies")
@PropertySource(value = "classpath:mvideo-parser.yaml",
        factory = YamlPropertySourceFactory.class)
public class CookieProperties {
    private Set<String> requiredNames;
}
