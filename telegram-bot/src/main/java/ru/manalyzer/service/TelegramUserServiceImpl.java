package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.manalyzer.storage.entity.TelegramUser;
import ru.manalyzer.storage.repository.TelegramUserRepository;

import java.util.Optional;

@Service
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    private final DatabaseUserService databaseUserService;

    @Autowired
    public TelegramUserServiceImpl(TelegramUserRepository telegramUserRepository, DatabaseUserService databaseUserService) {
        this.telegramUserRepository = telegramUserRepository;
        this.databaseUserService = databaseUserService;
    }

    public Optional<String> getUserIdByUserChat(String chatId) {
        Optional<TelegramUser> telegramUser = telegramUserRepository.findById(chatId);
        if (telegramUser.isPresent()) {
            return telegramUser.map(TelegramUser::getUserId);
        }

        Optional<String> userId = databaseUserService.findUserIdByChatId(chatId);
        if (userId.isPresent()) {
            telegramUserRepository.save(new TelegramUser(chatId, userId.get()));
            return userId;
        }

        return Optional.empty();
    }
}
