package ru.manalyzer.utility;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.manalyzer.property.CardSliderNavigationProperties;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardSliderNavigationButtonFormatter implements CardSliderButtonFormatter {

    private final CardSliderNavigationProperties cardSliderNavigationProperties;

    public CardSliderNavigationButtonFormatter(CardSliderNavigationProperties cardSliderNavigationProperties) {
        this.cardSliderNavigationProperties = cardSliderNavigationProperties;
    }

    private InlineKeyboardButton getButton(String text, CardButtonCommand callbackCommand) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackCommand.name())
                .build();
    }

    private InlineKeyboardButton getPreviousButton() {
        return getButton(cardSliderNavigationProperties.getButtonPreviousFormat(), CardButtonCommand.PREVIOUS);
    }

    private InlineKeyboardButton getNextButton() {
        return getButton(cardSliderNavigationProperties.getButtonNextFormat(), CardButtonCommand.NEXT);
    }

    private InlineKeyboardButton getCurrentPositionButton(int currentIndex, int size) {
        return getButton(String.format(cardSliderNavigationProperties.getButtonPageFormat(), currentIndex, size),
                CardButtonCommand.NO_ACTION);
    }

    @Override
    public InlineKeyboardMarkup format(ProductCardSlider productCardSlider) {
        // If Card Slider disable then return empty InlineKeyboardMarkup
        if (!productCardSlider.isActive()) {
            return InlineKeyboardMarkup.builder().build();
        }

        List<InlineKeyboardButton> navigationButtonRow = new ArrayList<>();
        if (hasPrevious(productCardSlider)) {
            navigationButtonRow.add(getPreviousButton());
        }
        navigationButtonRow.add(getCurrentPositionButton(productCardSlider.getCurrentPosition() + 1,
                getCardSliderSize(productCardSlider)));
        if (hasNext(productCardSlider)) {
            navigationButtonRow.add(getNextButton());
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(navigationButtonRow))
                .build();
    }

    private boolean hasNext(ProductCardSlider productCardSlider) {
        return productCardSlider.getCurrentPosition() < getCardSliderSize(productCardSlider) - 1;
    }

    private boolean hasPrevious(ProductCardSlider productCardSlider) {
        return productCardSlider.getCurrentPosition() > 0;
    }

    private int getCardSliderSize(ProductCardSlider productCardSlider) {
        return productCardSlider.getProducts().size();
    }
}
