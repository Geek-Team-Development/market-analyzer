package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.dto.NotifyMessageDto;

public interface NotificationService {
    Mono<NotificationDto> save(String userId, NotifyMessageDto notifyMessageDto);

    Flux<NotifyMessageDto> getNotifyMessages(String userLogin);

    void markAsRead(String userLogin, String notifyMessageId);
}
