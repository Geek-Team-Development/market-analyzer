package ru.manalyzer.entity;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class ProductCardButton implements CardButton {

    @Override
    public InlineKeyboardButton getPreviousButton() {
        return getButton("◀️", CardButtonCommand.PREVIOUS.name());
    }

    @Override
    public InlineKeyboardButton getNextButton() {
        return getButton("▶️️", CardButtonCommand.NEXT.name());
    }

    @Override
    public InlineKeyboardButton getCurrentPositionButton(int currentIndex, int size) {
        return getButton(String.format("%d из %d", currentIndex, size), CardButtonCommand.NO_ACTION.name());
    }

    private InlineKeyboardButton getButton(String text, String callback) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }
}
