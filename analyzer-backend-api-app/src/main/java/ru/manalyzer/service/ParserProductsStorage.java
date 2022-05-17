package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;

public interface ParserProductsStorage {
    void clear();
    Flux<ProductDto> getProducts(String searchName, Sort sort, int currentPage);

}