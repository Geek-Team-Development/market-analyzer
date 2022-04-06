package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final List<Parser> parsers;

    @Autowired
    public ProductServiceImpl(List<Parser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public Flux<ProductDto> findProducts(String searchName) {
        return Flux.combineLatest(subscribeParsersToSearchProducts(searchName), (arr) -> (ProductDto) arr[0]);
    }

    private List<Flux<ProductDto>> subscribeParsersToSearchProducts(String searchName) {
        return parsers.stream()
                .map(parser -> parser.parse(searchName))
                .collect(Collectors.toList());
    }
}
