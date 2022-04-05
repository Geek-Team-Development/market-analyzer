package ru.manalyzer.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource("classpath:oldi-connection.properties")
@ConfigurationProperties(prefix = "shop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OldiConnectionProperties {

    private String shopName;

    private String shopUri;

    private String searchUri;

    private String searchPath;

    private String searchParamName;

    private Map<String, String> queryParams = new HashMap<>();

}
