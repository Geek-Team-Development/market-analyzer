package ru.manalyzer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    @Bean
    public DirectExchange telegramExchange() {
        return new DirectExchange("telegram.exchange");
    }

    @Bean
    public Queue productTelegramQueue() {
        return new Queue("product.telegram.queue", true);
    }

    @Bean
    public Queue callbackTelegramQueue() {
        return new Queue("callback.telegram.queue", false);
    }

    @Bean
    public Queue messageTelegramQueue() {
        return new Queue("message.telegram.queue", true);
    }

    @Bean
    public Queue  commandTelegramQueue() {
        return new Queue("command.telegram.queue", true);
    }

    @Bean
    public Queue  authorizeTelegramQueue() {
        return new Queue("authorize.telegram.queue", true);
    }

    @Bean
    public Queue  notifyTelegramQueue() {
        return new Queue("notify.telegram.queue", true);
    }

    @Bean
    public Binding productTelegramBinding(@Qualifier("productTelegramQueue") Queue productTelegramQueue,
                                          DirectExchange telegramExchange) {
        return BindingBuilder
                .bind(productTelegramQueue)
                .to(telegramExchange)
                .with("product");
    }

    @Bean
    public Binding callbackTelegramBinding(@Qualifier("callbackTelegramQueue") Queue callbackTelegramQueue,
                                           DirectExchange telegramExchange) {
        return BindingBuilder
                .bind(callbackTelegramQueue)
                .to(telegramExchange)
                .with("callback");
    }

    @Bean
    public Binding messageTelegramBinding(@Qualifier("messageTelegramQueue") Queue messageTelegramQueue,
                                          DirectExchange telegramExchange) {
        return BindingBuilder
                .bind(messageTelegramQueue)
                .to(telegramExchange)
                .with("message");
    }

    @Bean
    public Binding commandTelegramBinding(@Qualifier("commandTelegramQueue") Queue commandTelegramQueue,
                                           DirectExchange telegramExchange) {
        return BindingBuilder
                .bind(commandTelegramQueue)
                .to(telegramExchange)
                .with("command");
    }

    @Bean
    public Binding authorizeTelegramBinding(@Qualifier("authorizeTelegramQueue") Queue authorizeTelegramQueue,
                                          DirectExchange telegramExchange) {
        return BindingBuilder
                .bind(authorizeTelegramQueue)
                .to(telegramExchange)
                .with("authorize");
    }

    @Bean
    public Binding notifyTelegramBinding(@Qualifier("notifyTelegramQueue") Queue notifyTelegramQueue,
                                            DirectExchange telegramExchange) {
        return BindingBuilder
                .bind(notifyTelegramQueue)
                .to(telegramExchange)
                .with("notify");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
