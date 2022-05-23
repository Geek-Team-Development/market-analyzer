package ru.manalyzer.telegram;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.service.TelegramService;


@Component
public class SpacePriceBotQueueListener {

    private final TelegramService telegramService;

    @Autowired
    public SpacePriceBotQueueListener(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @RabbitListener(queues = "product.telegram.queue")
    public void listenProductTelegram(ProductDto productDto) {
        System.out.println(productDto);
    }

    @RabbitListener(queues = "message.telegram.queue")
    public void listenMessageTelegram(Message message) {
        telegramService.newSearchRequest(message);
    }

    @RabbitListener(queues = "callback.telegram.queue")
    public void listenCallbackTelegram(CallbackQuery callbackQuery) {
        telegramService.callbackRequest(callbackQuery);
    }

    @RabbitListener(queues = "command.telegram.queue")
    public void listenCommandTelegram(Message message) {
        telegramService.commandRequest(message);
    }

    @RabbitListener(queues = "authorize.telegram.queue")
    public void listenAuthorizeTelegram(UserDto userDto) {
        telegramService.authorizeRequest(userDto);
    }
}
