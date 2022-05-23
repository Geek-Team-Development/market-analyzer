package ru.manalyzer.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.manalyzer.dto.UserDto;

public interface TelegramService {

    void newSearchRequest(Message message);

    void callbackRequest(CallbackQuery callbackQuery);

    void commandRequest(Message message);

    void authorizeRequest(UserDto userDto);
}
