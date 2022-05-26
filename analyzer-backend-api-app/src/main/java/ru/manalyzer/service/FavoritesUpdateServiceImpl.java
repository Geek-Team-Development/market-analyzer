package ru.manalyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.dto.NotifyMessageDto;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.persist.UserNotifyMessage;
import ru.manalyzer.repository.UserNotifyMessageRepository;
import ru.manalyzer.service.dto.MessageToFront;
import ru.manalyzer.service.dto.ProductUpdateDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class FavoritesUpdateServiceImpl implements FavoritesUpdateService {

    private final FavoritesService favoritesService;
    private final NotificationService notificationService;
    private final NotifyMessageService notifyMessageService;
    private final UserNotifyMessageRepository userNotifyMessageRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final Logger logger = LoggerFactory.getLogger(FavoritesUpdateServiceImpl.class);

    public FavoritesUpdateServiceImpl(FavoritesService favoritesService,
                                      NotificationService notificationService,
                                      NotifyMessageService notifyMessageService,
                                      UserNotifyMessageRepository userNotifyMessageRepository, RabbitTemplate rabbitTemplate) {
        this.favoritesService = favoritesService;
        this.notificationService = notificationService;
        this.notifyMessageService = notifyMessageService;
        this.userNotifyMessageRepository = userNotifyMessageRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
//    @Scheduled(fixedRateString = "${update.service.delayBetweenUpdate}",
//            initialDelayString = "${update.service.initialDelay}",
//            timeUnit = TimeUnit.HOURS)
    @Scheduled(fixedRateString = "40",
            initialDelayString = "0",
            timeUnit = TimeUnit.SECONDS)
    public void updateFavorites() {
        logger.info("Запустилось обновление продуктов из избранного");
//        favoritesService.update()
//                .subscribe(this::handleProductUpdated);
        mmm();
        logger.info("Закончилось обновление продуктов из избранного");
    }

//    @EventListener(ApplicationReadyEvent.class)
    public void mmm() {
//        Notification notification = new Notification();
//        NotifyMessage notifyMessage = new NotifyMessage();
//        notifyMessage.setId("1");
//        notifyMessage.setDate(new Date());
//        notifyMessage.setMessage("Message");
//        notification.setId("1");
//        notification.getMessages().add(notifyMessage);
//        notification.setUserId("user1");
//        notificationService.save(notificationMapper.toDto(notification)).subscribe();
        MessageToFront messageToFront = new MessageToFront();
        NotifyMessageDto notifyMessageDto = new NotifyMessageDto();
        notifyMessageDto.setMessage("Hello");
        notifyMessageDto.setDate(new Date());
        messageToFront.setNotifyMessage(notifyMessageDto);
        ProductDto oldProductDto = new ProductDto();
        oldProductDto.setId("pr1");
        oldProductDto.setName("name");
        oldProductDto.setProductLink("prod link1");
        oldProductDto.setImageLink("img link1");
        oldProductDto.setPrice("10000");
        oldProductDto.setShopName("MVideo");
        ProductDto newProductDto = new ProductDto();
        newProductDto.setId("pr1");
        newProductDto.setName("name");
        newProductDto.setProductLink("prod link1");
        newProductDto.setImageLink("img link1");
        newProductDto.setPrice("20000");
        newProductDto.setShopName("MVideo");
        ProductUpdateDto productUpdateDto = new ProductUpdateDto();
        productUpdateDto.setOldProductDto(oldProductDto);
        productUpdateDto.setNewProductDto(newProductDto);
        messageToFront.setObject(productUpdateDto);
        saveNotification("628fdf55fee8822a3ce811b2", productUpdateDto)
                .subscribe(notifyMessageDto1 -> {
                    sendMessage("628fdf55fee8822a3ce811b2", notifyMessageDto1, productUpdateDto);
                });
    }

    private void handleProductUpdated(ProductUpdateDto productUpdateDto) {
        favoritesService.getUsersWithProduct(productUpdateDto.getNewProductDto())
                .subscribe(userId -> {
                    saveNotification(userId, productUpdateDto)
                            .subscribe(savedNotifyMessageDto -> {
                                sendMessage(userId, savedNotifyMessageDto, productUpdateDto);
                            });
                });
    }

    private Mono<NotifyMessageDto> saveNotification(String userId, ProductUpdateDto productUpdateDto) {
        ProductDto oldProductDto = productUpdateDto.getOldProductDto();
        ProductDto newProductDto = productUpdateDto.getNewProductDto();
        String message = buildMessage(oldProductDto, newProductDto);
        NotifyMessageDto notifyMessageDto = new NotifyMessageDto();
        notifyMessageDto.setMessage(message);
        notifyMessageDto.setDate(new Date());
        return notifyMessageService.save(notifyMessageDto)
                .doOnNext(savedNotifyMessageDto -> {
                    UserNotifyMessage userNotifyMessage = new UserNotifyMessage();
                    userNotifyMessage.setUserId(userId);
                    userNotifyMessage.setNotifyMessageId(savedNotifyMessageDto.getId());
                    userNotifyMessage.setRead(false);
                    userNotifyMessageRepository.save(userNotifyMessage).subscribe();
                    notificationService.save(userId, savedNotifyMessageDto).subscribe();
                });
    }

    private void sendMessage(String userId, NotifyMessageDto notifyMessageDto,
                             ProductUpdateDto productUpdateDto) {
        MessageToFront messageToFront = new MessageToFront();
        messageToFront.setNotifyMessage(notifyMessageDto);
        messageToFront.setObject(productUpdateDto);
        rabbitTemplate.convertAndSend("front.notify.exchange", userId, messageToFront);
    }

    private String buildMessage(ProductDto oldProductDto, ProductDto newProductDto) {
        StringBuilder builder = new StringBuilder();
        if(!oldProductDto.getPrice().equals(newProductDto.getPrice())) {
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
        }
        return builder.toString();
    }
}
