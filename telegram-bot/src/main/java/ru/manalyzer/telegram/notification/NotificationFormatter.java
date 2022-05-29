package ru.manalyzer.telegram.notification;

import ru.manalyzer.dto.ProductDto;

public interface NotificationFormatter {

    String format(ProductDto productDto);
}
