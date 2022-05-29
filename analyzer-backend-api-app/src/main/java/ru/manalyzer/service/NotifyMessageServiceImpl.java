package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.manalyzer.dto.NotifyMessageDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.NotifyMessage;
import ru.manalyzer.repository.NotifyMessageRepository;

@Service
public class NotifyMessageServiceImpl implements NotifyMessageService {

    private final Mapper<NotifyMessage, NotifyMessageDto> notifyMessageMapper;

    private final NotifyMessageRepository notifyMessageRepository;

    @Autowired
    public NotifyMessageServiceImpl(Mapper<NotifyMessage, NotifyMessageDto> notifyMessageMapper,
                                    NotifyMessageRepository notifyMessageRepository) {
        this.notifyMessageMapper = notifyMessageMapper;
        this.notifyMessageRepository = notifyMessageRepository;
    }

    @Override
    public Mono<NotifyMessageDto> save(NotifyMessageDto notifyMessageDto) {
        NotifyMessage notifyMessage = notifyMessageMapper.toEntity(notifyMessageDto);
        return notifyMessageRepository.save(notifyMessage)
                .map(notifyMessageMapper::toDto);
    }
}
