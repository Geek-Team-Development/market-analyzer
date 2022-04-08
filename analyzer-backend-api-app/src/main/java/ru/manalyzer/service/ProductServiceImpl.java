package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.Parser;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final List<Parser> parsers;

    @Autowired
    public ProductServiceImpl(List<Parser> parsers) {
        this.parsers = parsers;
    }

    @Override
    public Flux<ProductDto> findProducts(ProductRequestParam requestParam) {
        return Flux.create(fluxSink -> {
            parsers.forEach(parser -> parser.parse(requestParam.getSearchName()).subscribe(fluxSink::next));

            fluxSink.complete();
        });
    }

}
