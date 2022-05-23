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
@PropertySource("classpath:authorization-connection.properties")
@ConfigurationProperties(value = "authorization")
public class AuthorizationConnectionProperties {

    private String uri;

    private String authorizationPath;

    private String chatIdParamName;
}
