package ru.manalyzer.service;

import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;

public interface TelegramService {

    void notifyChatIdUpdate(UserDto userDto);

    void notifyUsersAboutChangePrice(String chatId, ProductDto productDto);
}
