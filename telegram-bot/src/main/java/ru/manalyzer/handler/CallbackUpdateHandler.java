package ru.manalyzer.handler;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.manalyzer.property.AmqpProperties;

@Component
public class CallbackUpdateHandler extends UpdateHandler {

    protected CallbackUpdateHandler(AmqpProperties amqpProperties, AmqpTemplate amqpTemplate) {
        super(amqpProperties, amqpTemplate);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public void handle(Update update) {
        getAmqpTemplate().convertAndSend(
                getAmqpProperties().getTelegramExchange(),
                getAmqpProperties().getCallbackTelegramBindingRoutingKey(),
                update.getCallbackQuery()
        );
    }
}
