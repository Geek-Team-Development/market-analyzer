package ru.manalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Flux;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.NotificationMapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Notification;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.NotificationRepository;
import ru.manalyzer.repository.ReactiveFavoriteRepository;
import ru.manalyzer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class FavoritesUpdateServiceTest {

    private FavoritesUpdateService favoritesUpdateService;

    private final FavoritesService favoritesService = mock(FavoritesService.class);

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

    private final Mapper<Notification, NotificationDto> notificationMapper = mock(NotificationMapper.class);

    @BeforeEach
    public void init() {
        favoritesUpdateService = new FavoritesUpdateServiceImpl(favoritesService,
                notificationRepository, rabbitTemplate, notificationMapper);
    }

    @Test
    public void updateFavoritesTest() {

    }
}
