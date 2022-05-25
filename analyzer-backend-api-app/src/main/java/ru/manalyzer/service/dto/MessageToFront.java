package ru.manalyzer.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.manalyzer.dto.NotificationDto;

@Getter
@Setter
public class MessageToFront {
    private NotificationDto notification;
    private Object object;
}
