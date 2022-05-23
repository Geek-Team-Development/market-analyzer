package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.manalyzer.persist.AbstractPersistentObject;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.UserRepository;

import java.util.Optional;

@Component
public class DatabaseUserServiceImpl implements DatabaseUserService {

    private final UserRepository userRepository;

    @Autowired
    public DatabaseUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> findUserIdByChatId(String chatId) {
        Optional<User> user = userRepository.findByTelegramChatId(chatId);
        return user.map(AbstractPersistentObject::getId);
    }
}
