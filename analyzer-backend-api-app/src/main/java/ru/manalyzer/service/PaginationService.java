package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;

public interface PaginationService {
    Flux<ProductDto> findProducts(ProductRequestParam requestParam);
}
