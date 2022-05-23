package ru.manalyzer.service;

import java.util.Optional;

public interface TelegramUserService {

    Optional<String> getUserIdByUserChat(String chatId);
}
