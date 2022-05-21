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
@ConfigurationProperties(prefix = "mvideo.parser")
@PropertySource(value = "classpath:mvideo-parser.yaml",
        factory = YamlPropertySourceFactory.class)
public class ParserProperties {
    private String shopName;
    private String searchUrl;
    private String priceUrl;
    private String productDetailsUrl;
    private String imageLinkPrefix;
    private String productLinkPrefix;
    private IdsRequestProperties idsRequest;
}
