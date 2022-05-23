package ru.manalyzer.storage.repository;

import org.springframework.data.repository.CrudRepository;
import ru.manalyzer.storage.entity.TelegramUser;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, String> {
}
