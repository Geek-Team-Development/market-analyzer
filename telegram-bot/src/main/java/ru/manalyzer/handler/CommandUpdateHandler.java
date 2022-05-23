package ru.manalyzer.handler;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.manalyzer.property.AmqpProperties;

@Component
public class CommandUpdateHandler extends UpdateHandler {

    protected CommandUpdateHandler(AmqpProperties amqpProperties, AmqpTemplate amqpTemplate) {
        super(amqpProperties, amqpTemplate);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().isCommand();
    }

    @Override
    public void handle(Update update) {
        getAmqpTemplate().convertAndSend(
                getAmqpProperties().getTelegramExchange(),
                getAmqpProperties().getCommandTelegramBindingRoutingKey(),
                update.getMessage()
        );
    }
}
