package ru.manalyzer.parser.mvideo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.manalyzer.parser.mvideo.config.util.YamlPropertySourceFactory;

import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "mvideo")
@PropertySource(value = "classpath:mvideo-parser.yaml",
        factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class MVideoProperties {

    @Autowired
    private Cookies cookies;
    @Autowired
    private Connection connection;
    @Autowired
    private Headers headers;
    @Autowired
    private Parser parser;

    @Getter
    @Setter
    @Component
    @ConfigurationProperties(prefix = "mvideo.cookies")
    @PropertySource(value = "classpath:mvideo-parser.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class Cookies {
        private Set<String> requiredNames;
    }

    @Getter
    @Setter
    @Component
    @ConfigurationProperties(prefix = "mvideo.connection")
    @PropertySource(value = "classpath:mvideo-parser.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class Connection {
        private String baseUrl;
    }

    @Getter
    @Setter
    @Component
    @ConfigurationProperties(prefix = "mvideo.headers")
    @PropertySource(value = "classpath:mvideo-parser.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class Headers {
        private String host;
        private String userAgent;
        private String productDetailsReferer;
    }

    @Getter
    @Setter
    @Component
    @ConfigurationProperties(prefix = "mvideo.parser")
    @PropertySource(value = "classpath:mvideo-parser.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class Parser {
        private String shopName;
        private String searchUrl;
        private String priceUrl;
        private String productDetailsUrl;
        private String imageLinkPrefix;
        private String productLinkPrefix;
        @Autowired
        private IdsRequest idsRequest;

        @Getter
        @Setter
        @Component
        @ConfigurationProperties(prefix = "mvideo.ids-request")
        @PropertySource(value = "classpath:mvideo-parser.yaml",
                factory = YamlPropertySourceFactory.class)
        public static class IdsRequest {
            private String searchParamName;
            private Map<String, String> defaultParams;
        }
    }
}
