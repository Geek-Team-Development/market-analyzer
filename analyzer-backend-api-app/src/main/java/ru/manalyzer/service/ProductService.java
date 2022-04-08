package ru.manalyzer.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;

@Service
public interface ProductService {

    Flux<ProductDto> findProducts(ProductRequestParam requestParam);
}
