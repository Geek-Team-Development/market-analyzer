package ru.manalyzer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class NotificationDto extends AbstractPersistentDto {

    private String userId;

    private Set<NotifyMessageDto> messages = new HashSet<>();

}
