package ru.manalyzer.telegram.keyboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.List;

@Component
public class SpacePriceChatKeyboard implements ChatKeyboard {

    private final ChatButton spacePriceNavigationButton;

    private final ChatButton spacePriceFavoritesButton;

    @Autowired
    public SpacePriceChatKeyboard(ChatButton spacePriceNavigationButton, ChatButton spacePriceFavoritesButton) {
        this.spacePriceNavigationButton = spacePriceNavigationButton;
        this.spacePriceFavoritesButton = spacePriceFavoritesButton;
    }

    @Override
    public InlineKeyboardMarkup createKeyboard(ProductCardSlider productCardSlider) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        spacePriceNavigationButton.createButtons(productCardSlider),
                        spacePriceFavoritesButton.createButtons(productCardSlider)
                ))
                .build();


//        KeyboardRow row = new KeyboardRow();
//        row.add(new KeyboardButton("<a href=www.google.ru>Google</a>"));
//        List<KeyboardRow> keyboard = List.of(row);
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboard);
//        replyKeyboardMarkup.setSelective(true);
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        replyKeyboardMarkup.setOneTimeKeyboard(false);
//        return replyKeyboardMarkup;
    }
}
