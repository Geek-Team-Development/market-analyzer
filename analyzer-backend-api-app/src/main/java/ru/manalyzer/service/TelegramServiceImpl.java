package ru.manalyzer.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.manalyzer.dto.PriceNotificationDto;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.property.AmqpProperties;

import java.util.Optional;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final AmqpTemplate amqpTemplate;

    private final AmqpProperties amqpProperties;


    @Autowired
    public TelegramServiceImpl(AmqpTemplate amqpTemplate,
                               AmqpProperties amqpProperties) {
        this.amqpTemplate = amqpTemplate;
        this.amqpProperties = amqpProperties;
    }


    @Override
    public void notifyChatIdUpdate(UserDto userDto) {
        amqpTemplate.convertAndSend(
                amqpProperties.getTelegramExchange(),
                amqpProperties.getAuthorizeTelegramBindingRoutingKey(),
                userDto
        );
    }

    @Override
    public void notifyUsersAboutChangePrice(String chatId, ProductDto productDto) {
         amqpTemplate.convertAndSend(
                amqpProperties.getTelegramExchange(),
                amqpProperties.getNotifyTelegramBindingRoutingKey(),
                new PriceNotificationDto(chatId, productDto)
         );
    }
}
