package ru.manalyzer.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;

import java.util.List;

@Service
public interface ProductService {

    Flux<ProductDto> findProducts(ProductRequestParam requestParam);

    Flux<ProductDto> findProducts(String parserName, ProductRequestParam requestParam);

    List<String> getParserNames();

}
