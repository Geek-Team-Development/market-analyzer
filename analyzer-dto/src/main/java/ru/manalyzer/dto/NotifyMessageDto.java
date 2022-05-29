package ru.manalyzer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotifyMessageDto extends AbstractPersistentDto {

    private String message;

    private Date date;

}
