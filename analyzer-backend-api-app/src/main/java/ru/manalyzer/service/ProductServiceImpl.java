package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.Parser;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductServiceImpl implements ProductService {

    private final Map<String, Parser> activeParserMap;

    @Autowired
    public ProductServiceImpl(@Qualifier("activeParserMap") Map<String, Parser> activeParserMap) {
        this.activeParserMap = activeParserMap;
    }

    @Override
    public Flux<ProductDto> findProducts(ProductRequestParam requestParam) {
        AtomicInteger counter = new AtomicInteger(activeParserMap.size());
        return Flux.create(fluxSink -> activeParserMap.values().forEach(parser -> parser.parse(requestParam.getSearchName(), requestParam.getSort().orElse(Sort.price_asc), requestParam.getPageNumber().orElse("0"))
                .doOnComplete(() -> {
                    if (counter.decrementAndGet() == 0) {
                        fluxSink.complete();
                    }
                })
                .subscribe(fluxSink::next)));
    }

    @Override
    public Flux<ProductDto> findProducts(String parserName, ProductRequestParam requestParam) {
        Parser neededParser = activeParserMap.get(parserName);
        return Flux.create(fluxSink -> neededParser.parse(requestParam.getSearchName(), requestParam.getSort().orElse(Sort.price_asc), requestParam.getPageNumber().orElse("0"))
                .doOnComplete(fluxSink::complete)
                .subscribe(fluxSink::next));
    }

    @Override
    public List<String> getParserNames() {
        return activeParserMap.values()
                .stream()
                .map(Parser::getShopName)
                .collect(Collectors.toList());
    }

}
