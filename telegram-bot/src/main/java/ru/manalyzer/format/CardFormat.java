package ru.manalyzer.format;

import ru.manalyzer.dto.ProductDto;

public interface CardFormat {

    String format(ProductDto productDto);
}
