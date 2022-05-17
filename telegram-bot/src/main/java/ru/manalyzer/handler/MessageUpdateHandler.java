package ru.manalyzer.handler;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.manalyzer.property.AmqpProperties;

@Component
public class MessageUpdateHandler extends UpdateHandler {

    @Autowired
    protected MessageUpdateHandler(AmqpProperties amqpProperties, AmqpTemplate amqpTemplate) {
        super(amqpProperties, amqpTemplate);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage();
    }

    @Override
    public void handle(Update update) {
        getAmqpTemplate().convertAndSend(
                getAmqpProperties().getTelegramExchange(),
                getAmqpProperties().getMessageTelegramBindingRoutingKey(),
                update.getMessage()
        );
    }
}
