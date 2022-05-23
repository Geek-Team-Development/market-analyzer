package ru.manalyzer.parser.mvideo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.parser.mvideo.config.util.YamlPropertySourceFactory;

@Component
@ConfigurationProperties(prefix = "mvideo")
@PropertySource(value = "classpath:mvideo-parser.yaml",
        factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class MVideoProperties {
    private CookieProperties cookies;
    private ConnectionProperties connection;
    private HeadersProperties headers;
    private ParserProperties parser;
}
