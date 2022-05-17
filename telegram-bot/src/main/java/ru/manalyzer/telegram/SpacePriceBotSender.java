package ru.manalyzer.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.utility.CardSliderButtonFormatter;
import ru.manalyzer.utility.CardSliderFormatter;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.Optional;

@Component
public class SpacePriceBotSender {

    private final SpacePriceBot spacePriceBot;

    private final CardSliderFormatter cardSliderCaptionFormatter;

    private final CardSliderButtonFormatter cardSliderNavigationButtonFormatter;

    public SpacePriceBotSender(SpacePriceBot spacePriceBot,
                               CardSliderFormatter cardSliderCaptionFormatter,
                               CardSliderButtonFormatter cardSliderNavigationButtonFormatter) {
        this.spacePriceBot = spacePriceBot;
        this.cardSliderCaptionFormatter = cardSliderCaptionFormatter;
        this.cardSliderNavigationButtonFormatter = cardSliderNavigationButtonFormatter;
    }

    public Optional<Message> sendProductCardSlider(ProductCardSlider productCardSlider) {
        if (productCardSlider.getMessageId() == null) {
            return Optional.ofNullable(sendNewProductCardSlider(productCardSlider));
        } else {
            updateProductCardSlider(productCardSlider);
            return Optional.empty();
        }
    }

    private void updateProductCardSlider(ProductCardSlider productCardSlider) {
        ProductDto currentProducts = productCardSlider.navigator().getCurrentProduct();
        EditMessageMedia messageMedia = EditMessageMedia.builder()
                .chatId(productCardSlider.getId())
                .messageId(productCardSlider.getMessageId())
                .media(InputMediaPhoto.builder()
                        .media(currentProducts.getImageLink())
                        .caption(cardSliderCaptionFormatter.format(productCardSlider))
                        .parseMode(ParseMode.HTML)
                        .build())
                .replyMarkup(cardSliderNavigationButtonFormatter.format(productCardSlider))
                .build();

        updateMessageMedia(messageMedia);
    }

    private Message sendNewProductCardSlider(ProductCardSlider productCardSlider) {
        ProductDto currentProducts = productCardSlider.navigator().getCurrentProduct();
        SendPhoto photo = SendPhoto.builder()
                .chatId(productCardSlider.getId())
                .photo(new InputFile(currentProducts.getImageLink()))
                .caption(cardSliderCaptionFormatter.format(productCardSlider))
                .replyMarkup(cardSliderNavigationButtonFormatter.format(productCardSlider))
                .parseMode(ParseMode.HTML)
                .build();

        return sendPhoto(photo);
    }

    private synchronized Message sendPhoto(SendPhoto photo) {
        try {
            return spacePriceBot.execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    private synchronized void updateMessageMedia(EditMessageMedia messageMedia) {
        try {
            spacePriceBot.execute(messageMedia);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
