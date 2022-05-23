package ru.manalyzer.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("TelegramUser")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TelegramUser {

    // chatId
    private String id;

    private String userId;
}
