package ru.manalyzer.parser.mvideo.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.parser.mvideo.config.util.YamlPropertySourceFactory;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mvideo.parser.ids-request")
@PropertySource(value = "classpath:mvideo-parser.yaml",
        factory = YamlPropertySourceFactory.class)
public class IdsRequestProperties {
    private String searchParamName;
    private String sortParamName;
    private String offsetParamName;
    private List<String> filterParamNames;
    private Map<String, String> filterParams;
    private String filterParamName;
    private Map<String, String> defaultParams;
}
