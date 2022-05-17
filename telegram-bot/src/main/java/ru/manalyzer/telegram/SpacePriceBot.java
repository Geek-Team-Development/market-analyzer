package ru.manalyzer.telegram;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.manalyzer.handler.UpdateHandler;
import ru.manalyzer.property.TelegramProperties;

import java.util.List;


@Log4j2
public class SpacePriceBot extends TelegramLongPollingBot {

    private final TelegramProperties telegramProperty;

    private final List<UpdateHandler> updateHandlers;

    @Autowired
    @SneakyThrows
    public SpacePriceBot(TelegramProperties telegramProperty,
                         List<UpdateHandler> updateHandlers) {
        this.telegramProperty = telegramProperty;
        this.updateHandlers = updateHandlers;
    }

    @Override
    public String getBotUsername() {
        return telegramProperty.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramProperty.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateHandlers.stream()
                .filter(handler -> handler.canHandle(update))
                .forEach(handler -> handler.handle(update));
    }

}
