package ru.manalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.dto.NotifyMessageDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.NotificationMapper;
import ru.manalyzer.mapper.NotifyMessageMapper;
import ru.manalyzer.persist.Notification;
import ru.manalyzer.persist.NotifyMessage;
import ru.manalyzer.repository.NotifyMessageRepository;

import static org.mockito.Mockito.*;

public class FavoritesUpdateServiceTest {

    private FavoritesUpdateService favoritesUpdateService;

    private final FavoritesService favoritesService = mock(FavoritesService.class);

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

    @BeforeEach
    public void init() {
//        favoritesUpdateService = new FavoritesUpdateServiceImpl(
//                favoritesService, notifyMessageRepository,
//                reactiveNotificationRepository, notificationRepository,
//                rabbitTemplate, notifyMessageMapper, notificationMapper);
    }

    @Test
    public void updateFavoritesTest() {

    }
}
