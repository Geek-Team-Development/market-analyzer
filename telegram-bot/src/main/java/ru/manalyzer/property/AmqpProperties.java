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
@PropertySource("classpath:amqp.properties")
@ConfigurationProperties(value = "amqp")
public class AmqpProperties {

    private String telegramExchange;

    private String productTelegramQueue;

    private String callbackTelegramQueue;

    private String messageTelegramQueue;

    private String commandTelegramQueue;

    private String authorizeTelegramQueue;

    private String notifyTelegramQueue;

    private String productTelegramBindingRoutingKey;

    private String callbackTelegramBindingRoutingKey;

    private String messageTelegramBindingRoutingKey;

    private String commandTelegramBindingRoutingKey;

    private String authorizeTelegramBindingRoutingKey;

    private String notifyTelegramBindingRoutingKey;
}
