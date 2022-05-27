package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.dto.NotifyMessageDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.Notification;
import ru.manalyzer.persist.NotifyMessage;
import ru.manalyzer.repository.NotificationRepository;
import ru.manalyzer.repository.UserNotifyMessageRepository;
import ru.manalyzer.service.dto.MessageToFront;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Mapper<Notification, NotificationDto> notificationMapper;
    private final Mapper<NotifyMessage, NotifyMessageDto> notifyMessageMapper;
    private final NotificationRepository notificationRepository;
    private final AuthenticationService authenticationService;
    private final UserNotifyMessageRepository userNotifyMessageRepository;
    private final NotifyMessageService notifyMessageService;

    @Autowired
    public NotificationServiceImpl(Mapper<Notification, NotificationDto> notificationMapper,
                                   Mapper<NotifyMessage, NotifyMessageDto> notifyMessageMapper, NotificationRepository notificationRepository,
                                   AuthenticationService authenticationService, UserNotifyMessageRepository userNotifyMessageRepository, NotifyMessageService notifyMessageService) {
        this.notificationMapper = notificationMapper;
        this.notifyMessageMapper = notifyMessageMapper;
        this.notificationRepository = notificationRepository;
        this.authenticationService = authenticationService;
        this.userNotifyMessageRepository = userNotifyMessageRepository;
        this.notifyMessageService = notifyMessageService;
    }

    @Override
    public Mono<NotificationDto> save(String userId, NotifyMessageDto notifyMessageDto) {
        return notificationRepository.findByUserId(userId)
                .flatMap(notification -> {
                    notification.getMessages().add(notifyMessageMapper.toEntity(notifyMessageDto));
                    return notificationRepository.save(notification)
                            .map(notificationMapper::toDto);
                })
                .switchIfEmpty(saveNewNotification(userId, notifyMessageDto)
                        .map(notificationMapper::toDto));
    }

    private Mono<Notification> saveNewNotification(String userId, NotifyMessageDto notifyMessageDto) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.getMessages().add(notifyMessageMapper.toEntity(notifyMessageDto));
        return notificationRepository.save(notification);
    }

    @Override
    public Flux<MessageToFront> getNotifyMessages(String userLogin) {
        UserDto userDto = authenticationService.findUserByEmail(userLogin);
        return notificationRepository.findByUserId(userDto.getId())
                .flatMapIterable(Notification::getMessages)
                .map(notifyMessageMapper::toDto)
                .flatMap(notifyMessageDto ->
                        userNotifyMessageRepository
                                .findByUserIdAndNotifyMessageId(userDto.getId(),
                                        notifyMessageDto.getId())
                                .map(userNotifyMessage -> {
                                    MessageToFront message = new MessageToFront();
                                    message.setNotifyMessage(notifyMessageDto);
                                    message.setRead(userNotifyMessage.isRead());
                                    return message;
                        }));
    }

    @Override
    public void markAsRead(String userLogin, String notifyMessageId) {
        UserDto userDto = authenticationService.findUserByEmail(userLogin);
        userNotifyMessageRepository.findByUserIdAndNotifyMessageId(userDto.getId(), notifyMessageId)
                .subscribe(userNotifyMessage -> {
                    userNotifyMessage.setRead(true);
                    userNotifyMessageRepository.save(userNotifyMessage).subscribe();
                });
    }
}
