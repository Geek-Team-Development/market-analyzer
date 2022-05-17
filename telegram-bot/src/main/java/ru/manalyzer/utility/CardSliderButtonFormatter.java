package ru.manalyzer.utility;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.manalyzer.storage.entity.ProductCardSlider;

public interface CardSliderButtonFormatter {

    InlineKeyboardMarkup format(ProductCardSlider productCardSlider);
}
