package ru.manalyzer.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.manalyzer.dto.NotifyMessageDto;

@Getter
@Setter
public class MessageToFront {
    private NotifyMessageDto notifyMessage;
    private Object object;
    private boolean read;
}
