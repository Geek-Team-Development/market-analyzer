package ru.manalyzer.service;

import ru.manalyzer.service.dto.ProductUpdateDto;

public interface ProductUpdateListener {
    void update(ProductUpdateDto productUpdateDto);
}
