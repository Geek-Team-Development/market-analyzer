package ru.manalyzer.telegram.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.List;

public interface ChatButton {

    List<InlineKeyboardButton> createButtons(ProductCardSlider productCardSlider);
}
