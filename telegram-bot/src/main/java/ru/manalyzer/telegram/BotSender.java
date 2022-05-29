package ru.manalyzer.telegram;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.Optional;

public interface BotSender {

    Optional<Message> sendProductCardSlider(ProductCardSlider productCardSlider);

    void sendInformationMessage(String chatId, String messageText);

    void sendPriceNotification(String chatId, ProductDto productDto);
}
