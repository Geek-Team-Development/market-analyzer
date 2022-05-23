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

    private String authorizeTelegramQueue;

    private String messageTelegramQueue;

    private String notifyTelegramQueue;

    private String authorizeTelegramBindingRoutingKey;

    private String messageTelegramBindingRoutingKey;

    private String notifyTelegramBindingRoutingKey;
}