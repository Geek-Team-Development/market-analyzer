package ru.manalyzer.listner;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.manalyzer.entity.CardButtonCommand;
import ru.manalyzer.entity.CardSlider;
import ru.manalyzer.format.CardFormat;
import ru.manalyzer.property.TelegramProperty;
import ru.manalyzer.service.ProductService;
import ru.manalyzer.storage.ChatStorage;

import java.util.Optional;


@Component
@Log4j2
public class SpacePriceBot extends TelegramLongPollingBot {

    private final TelegramProperty telegramProperty;

    private final ProductService productService;

    private final ChatStorage chatStorage;

    private final CardFormat cardFormat;

    @Autowired
    @SneakyThrows
    public SpacePriceBot(TelegramProperty telegramProperty,
                         TelegramBotsApi telegramBotsApi,
                         ProductService productService,
                         ChatStorage chatStorage,
                         CardFormat cardFormat) {
        this.telegramProperty = telegramProperty;
        this.productService = productService;
        this.chatStorage = chatStorage;
        this.cardFormat = cardFormat;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return telegramProperty.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramProperty.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        } else if (update.hasMessage()) {
            handleSearchQuery(update);
        }
    }

    private void handleCallbackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String chatId = callbackQuery.getFrom().getId().toString();
        CardSlider cardSlider = chatStorage.getCardSlider(chatId).orElseThrow(IllegalArgumentException::new);

        switch (CardButtonCommand.valueOf(callbackQuery.getData())) {
            case NEXT:
                cardSlider.next();
                sendCard(chatId, cardSlider);
                break;
            case PREVIOUS:
                cardSlider.previous();
                sendCard(chatId, cardSlider);
                break;
        }
    }

    private void handleSearchQuery(Update update) {
        Message message = update.getMessage();
        CardSlider productSlider = resetCardSlider(message.getChatId().toString());
        if (message.hasText()) {
            productService.findProducts(message.getText())
                    .subscribe(productDto -> {
                        if (productSlider.add(productDto)) {
                            sendCard(message.getChatId().toString(), productSlider);
                        }
                    });
        }
    }

    private CardSlider resetCardSlider(String chatId) {
        Optional<CardSlider> cardSliderOpt = chatStorage.getCardSlider(chatId);
        if (cardSliderOpt.isPresent()) {
            CardSlider cardSlider = cardSliderOpt.get();
            cardSlider.disableKeyboard();
            try {
                execute(update(chatId, cardSlider));
            } catch (TelegramApiException e) {
                log.error("Keyboard not removed", e);
            }
        }

        return chatStorage.createCardSlider(chatId);
    }

    private void sendCard(String chatId, CardSlider cardSlider) {
        try {
            if (cardSlider.size() == 1) {
                Message message = execute(insert(chatId, cardSlider));
                cardSlider.setMessageId(message.getMessageId());
            } else {
                execute(update(chatId, cardSlider));
            }
        } catch (TelegramApiException e) {
            log.error("Card not sent", e);
        }
    }

    private EditMessageMedia update(String chatId, CardSlider cardSlider) {
        return EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(cardSlider.getMessageId())
                .media(InputMediaPhoto.builder()
                        .media(cardSlider.getPhotoLink())
                        .caption(cardSlider.getCaption())
                        .parseMode(ParseMode.HTML)
                        .build())
                .replyMarkup(cardSlider.getKeyboard())
                .build();
    }

    private SendPhoto insert(String chatId, CardSlider cardSlider) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(cardSlider.getPhoto())
                .caption(cardSlider.getCaption())
                .replyMarkup(cardSlider.getKeyboard())
                .parseMode(ParseMode.HTML)
                .build();
    }
}
