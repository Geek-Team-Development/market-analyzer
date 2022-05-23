package ru.manalyzer.telegram.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.manalyzer.storage.entity.ProductCardSlider;

public interface ChatKeyboard {

    InlineKeyboardMarkup createKeyboard(ProductCardSlider productCardSlider);
}
