package ru.manalyzer.service;

import reactor.core.publisher.Mono;
import ru.manalyzer.dto.NotifyMessageDto;

public interface NotifyMessageService {
    Mono<NotifyMessageDto> save(NotifyMessageDto notifyMessageDto);
}
