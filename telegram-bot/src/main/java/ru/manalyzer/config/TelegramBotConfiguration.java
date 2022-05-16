package ru.manalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.manalyzer.handler.UpdateHandler;
import ru.manalyzer.property.TelegramProperties;
import ru.manalyzer.telegram.SpacePriceBot;

import java.util.List;

@Configuration
public class TelegramBotConfiguration {

    private final TelegramProperties telegramProperties;

    private final List<UpdateHandler> updateHandlers;

    public TelegramBotConfiguration(TelegramProperties telegramProperties, List<UpdateHandler> updateHandlers) {
        this.telegramProperties = telegramProperties;
        this.updateHandlers = updateHandlers;
    }

    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public SpacePriceBot spacePriceBot() throws TelegramApiException {
        SpacePriceBot spacePriceBot = new SpacePriceBot(telegramProperties, updateHandlers);
        telegramBotsApi().registerBot(spacePriceBot);
        return spacePriceBot;
    }
}
