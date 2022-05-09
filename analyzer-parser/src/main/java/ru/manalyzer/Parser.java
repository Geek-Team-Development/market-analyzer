package ru.manalyzer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.dto.ProductDto;

public interface Parser {
    Flux<ProductDto> parse(String searchName);

    Mono<ProductDto> parseOneProduct(ProductDto productDto);

    Mono<ProductDto> parseProductPage(ProductDto productDto);

    String getShopName();
}
