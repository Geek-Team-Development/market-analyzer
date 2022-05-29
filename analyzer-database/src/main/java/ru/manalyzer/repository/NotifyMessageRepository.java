package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.manalyzer.persist.NotifyMessage;

public interface NotifyMessageRepository extends ReactiveMongoRepository<NotifyMessage, String> {
}
