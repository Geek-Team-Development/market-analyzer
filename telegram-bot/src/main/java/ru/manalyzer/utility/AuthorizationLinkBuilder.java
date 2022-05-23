package ru.manalyzer.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.manalyzer.property.AuthorizationConnectionProperties;

@Component
public class AuthorizationLinkBuilder {

    private String chatId;

    private final AuthorizationConnectionProperties authorizationConnectionProperties;

    @Autowired
    public AuthorizationLinkBuilder(AuthorizationConnectionProperties authorizationConnectionProperties) {
        this.authorizationConnectionProperties = authorizationConnectionProperties;
    }

    public AuthorizationLinkBuilder withChatId(String chatId) {
        this.chatId = chatId;
        return this;
    }

    public String build() {
        return String.format("%s%s?%s=%s",
                authorizationConnectionProperties.getUri(),
                authorizationConnectionProperties.getAuthorizationPath(),
                authorizationConnectionProperties.getChatIdParamName(),
                chatId);
    }
}
