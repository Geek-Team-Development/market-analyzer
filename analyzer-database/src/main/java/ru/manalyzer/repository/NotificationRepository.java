package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.manalyzer.persist.Notification;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
}
