package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;

public interface ProductSearchService {

    Flux<ProductDto> findProducts(String searchName);
}
