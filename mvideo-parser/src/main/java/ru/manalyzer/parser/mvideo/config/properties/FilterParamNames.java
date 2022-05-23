package ru.manalyzer.parser.mvideo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.parser.mvideo.config.util.YamlPropertySourceFactory;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mvideo.parser.ids-request.filter-param-names")
@PropertySource(value = "classpath:mvideo-parser.yaml",
        factory = YamlPropertySourceFactory.class)
public class FilterParamNames {
    private String availableToSale;
}