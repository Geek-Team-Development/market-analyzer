package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.persist.UserNotifyMessage;

public interface UserNotifyMessageRepository
        extends ReactiveMongoRepository<UserNotifyMessage, String> {
    Flux<UserNotifyMessage> findByUserId(String userId);

    Mono<UserNotifyMessage> findByUserIdAndNotifyMessageId(String userId, String notifyMessageId);
}
