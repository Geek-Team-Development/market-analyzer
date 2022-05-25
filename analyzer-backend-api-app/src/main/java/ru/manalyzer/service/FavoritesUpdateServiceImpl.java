package ru.manalyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.NotificationMapper;
import ru.manalyzer.persist.Notification;
import ru.manalyzer.repository.NotificationRepository;
import ru.manalyzer.service.dto.MessageToFront;
import ru.manalyzer.service.dto.ProductUpdateDto;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class FavoritesUpdateServiceImpl implements FavoritesUpdateService {

    private final FavoritesService favoritesService;
    private final NotificationRepository notificationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Mapper<Notification, NotificationDto> notificationMapper;

    private static final Logger logger = LoggerFactory.getLogger(FavoritesUpdateServiceImpl.class);

    public FavoritesUpdateServiceImpl(FavoritesService favoritesService,
                                      NotificationRepository notificationRepository,
                                      RabbitTemplate rabbitTemplate,
                                      Mapper<Notification, NotificationDto> notificationMapper) {
        this.favoritesService = favoritesService;
        this.rabbitTemplate = rabbitTemplate;
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Scheduled(fixedRateString = "${update.service.delayBetweenUpdate}",
            initialDelayString = "${update.service.initialDelay}",
            timeUnit = TimeUnit.HOURS)
    public void updateFavorites() {
        logger.info("Запустилось обновление продуктов из избранного");
        favoritesService.update()
                .subscribe(productUpdateDto -> {
                    favoritesService.getUsersWithProduct(productUpdateDto.getNewProductDto())
                            .subscribe(userId -> {
                                sendMessage(userId, productUpdateDto);
                            });
                });
        logger.info("Закончилось обновление продуктов из избранного");
    }

    private void sendMessage(String userId, ProductUpdateDto productUpdateDto) {
        ProductDto oldProductDto = productUpdateDto.getOldProductDto();
        ProductDto newProductDto = productUpdateDto.getNewProductDto();
        if(!oldProductDto.getPrice().equals(newProductDto.getPrice())) {
            StringBuilder builder = new StringBuilder();
            BigDecimal oldPrice = new BigDecimal(oldProductDto.getPrice());
            BigDecimal newPrice = new BigDecimal(newProductDto.getPrice());
            if(newPrice.compareTo(oldPrice) < 1) {
                builder.append("Уменьшилась цена на ");
            } else {
                builder.append("Увеличилась цена на ");
            }
            builder.append("продукт ")
                    .append(newProductDto.getName())
                    .append(" c ").append(oldProductDto.getPrice())
                    .append(" на ").append(newProductDto.getPrice());
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(builder.toString());
            notificationRepository.save(notification)
                            .subscribe(savedNotification -> {
                                NotificationDto notificationDto = notificationMapper.toDto(savedNotification);
                                MessageToFront message = new MessageToFront();
                                message.setNotification(notificationDto);
                                message.setObject(productUpdateDto);
                                rabbitTemplate.convertAndSend("front.notify.exchange", userId, message);
                            });
        }
    }
}
