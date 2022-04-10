package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.Parser;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductServiceImpl implements ProductService {

    private final List<Parser> parsers;

    private final AtomicInteger counter;

    @Autowired
    public ProductServiceImpl(List<Parser> parsers) {
        this.parsers = parsers;
        this.counter = new AtomicInteger();
    }

    @Override
    public Flux<ProductDto> findProducts(ProductRequestParam requestParam) {
        counter.set(parsers.size());
        return Flux.create(fluxSink -> parsers.forEach(parser -> parser.parse(requestParam.getSearchName())
                .doOnComplete(() -> {
                    if (counter.decrementAndGet() == 0) {
                        fluxSink.complete();
                    }
                })
                .subscribe(fluxSink::next)));
    }

}
