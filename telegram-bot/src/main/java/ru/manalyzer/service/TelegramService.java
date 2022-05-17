package ru.manalyzer.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TelegramService {

    void newSearchRequest(Message message);

    void callbackRequest(CallbackQuery callbackQuery);
}
