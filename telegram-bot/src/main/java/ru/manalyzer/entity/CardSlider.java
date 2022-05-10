package ru.manalyzer.entity;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.manalyzer.dto.ProductDto;

public interface CardSlider {

    boolean add(ProductDto productDto);

    int size();

    InputFile getPhoto();

    String getPhotoLink();

    String getCaption();

    Integer getMessageId();

    void setMessageId(Integer messageId);

    void next();

    void previous();

    boolean hasNext();

    boolean hasPrevious();

    InlineKeyboardMarkup getKeyboard();

    void enableKeyboard();

    void disableKeyboard();
}
