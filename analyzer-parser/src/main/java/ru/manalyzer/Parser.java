package ru.manalyzer;

import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;

public interface Parser {
    Flux<ProductDto> parse(String searchName);
}
