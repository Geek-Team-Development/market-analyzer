package ru.manalyzer.service;

import ru.manalyzer.persist.User;

import java.util.Optional;

public interface DatabaseUserService {

    Optional<String> findUserIdByChatId(String chatId);
}
