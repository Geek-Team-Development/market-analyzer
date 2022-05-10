package ru.manalyzer.entity;

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.format.CardFormat;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ProductCardSlider implements CardSlider {

    private final CardFormat cardFormat;

    private final CardButton cardButton;

    private List<ProductDto> products;

    private int currentProductIndex;

    private Integer messageId;

    private boolean isEnableKeyboard;

    public ProductCardSlider(CardFormat cardFormat, CardButton cardButton) {
        this.cardFormat = cardFormat;
        this.cardButton = cardButton;
        products = new ArrayList<>();
        this.isEnableKeyboard = true;
    }

    @Override
    public boolean add(ProductDto productDto) {
        return products.add(productDto);
    }

    @Override
    public int size() {
        return products.size();
    }

    @Override
    public InputFile getPhoto() {
        return new InputFile(products.get(currentProductIndex).getImageLink());
    }

    @Override
    public String getPhotoLink() {
        return products.get(currentProductIndex).getImageLink();
    }

    @Override
    public String getCaption() {
        return cardFormat.format(products.get(currentProductIndex));
    }

    @Override
    public Integer getMessageId() {
        return messageId;
    }

    @Override
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    @Override
    public void next() {
        if (hasNext()) {
            currentProductIndex++;
        }
    }

    @Override
    public void previous() {
        if (hasPrevious()) {
            currentProductIndex--;
        }
    }

    @Override
    public boolean hasNext() {
        return currentProductIndex < products.size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return currentProductIndex > 0;
    }

    @Override
    public InlineKeyboardMarkup getKeyboard() {
        List<InlineKeyboardButton> navigationButtonRow = new ArrayList<>();
        if (isEnableKeyboard) {
            if (hasPrevious()) {
                navigationButtonRow.add(cardButton.getPreviousButton());
            }
            navigationButtonRow.add(cardButton.getCurrentPositionButton(currentProductIndex + 1, size()));
            if (hasNext()) {
                navigationButtonRow.add(cardButton.getNextButton());
            }
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(navigationButtonRow))
                .build();
    }

    @Override
    public void enableKeyboard() {
        isEnableKeyboard = true;
    }

    @Override
    public void disableKeyboard() {
        isEnableKeyboard = false;
    }
}
