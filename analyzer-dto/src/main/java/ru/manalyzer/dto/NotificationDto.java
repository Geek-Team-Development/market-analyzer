package ru.manalyzer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto extends AbstractPersistentDto {

    private String userId;

    private String message;

}
