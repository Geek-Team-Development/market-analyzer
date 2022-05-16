package ru.manalyzer.handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.AmqpTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.manalyzer.property.AmqpProperties;

@Getter
@Setter
public abstract class UpdateHandler {

    private final AmqpProperties amqpProperties;

    private final AmqpTemplate amqpTemplate;

    protected UpdateHandler(AmqpProperties amqpProperties, AmqpTemplate amqpTemplate) {
        this.amqpProperties = amqpProperties;
        this.amqpTemplate = amqpTemplate;
    }

    public abstract boolean canHandle(Update update);

    public abstract void handle(Update update);
}
