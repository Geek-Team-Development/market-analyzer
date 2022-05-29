package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.manalyzer.persist.Notification;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

    Mono<Notification> findByUserId(String userId);

}
