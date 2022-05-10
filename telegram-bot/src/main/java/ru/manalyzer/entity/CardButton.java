package ru.manalyzer.entity;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface CardButton {

    InlineKeyboardButton getPreviousButton();

    InlineKeyboardButton getNextButton();

    InlineKeyboardButton getCurrentPositionButton(int currentIndex, int size);
}
