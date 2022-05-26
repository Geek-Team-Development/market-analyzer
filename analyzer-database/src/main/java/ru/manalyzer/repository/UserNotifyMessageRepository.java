package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.manalyzer.persist.UserNotifyMessage;

public interface UserNotifyMessageRepository
        extends ReactiveMongoRepository<UserNotifyMessage, String> {
    Mono<UserNotifyMessage> findByUserIdAndNotifyMessageId(String userId, String notifyMessageId);
}
