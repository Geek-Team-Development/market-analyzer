package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@SessionScope
public class ParserProductsStorageImpl implements ParserProductsStorage {
    private final Map<String, ParserProducts> productDtoMap = new ConcurrentHashMap<>();

    private final ProductService productService;

    @Autowired
    public ParserProductsStorageImpl(ProductService productService) {
        this.productService = productService;
        productService.getParserNames().forEach(parserName -> {
                    ParserProducts parserProducts = new ParserProducts();
                    parserProducts.setParserName(parserName);
                    productDtoMap.put(parserName, parserProducts);
                }
        );
    }

    public void clear() {
        productDtoMap.values().forEach(ParserProducts::clear);
    }

    public Flux<ProductDto> getProducts(String searchName, Sort sort, int currentPage) {
        return findNewProducts(searchName, sort, currentPage);
    }

    private Flux<ProductDto> findNewProducts(String searchName, Sort sort, int currentPage) {
        List<String> neededParsers = productDtoMap.values()
                .stream()
                .filter(parserProducts -> parserProducts.getSize() < 10)
                .map(ParserProducts::getParserName)
                .collect(Collectors.toList());
        AtomicInteger count = new AtomicInteger(neededParsers.size());
        return Flux.create(fluxSink -> {
            if(neededParsers.size() > 0) {
                neededParsers.forEach(parserName -> {
                    ParserProducts parserProducts = productDtoMap.get(parserName);
                    ProductRequestParam requestParam = getProductRequestParamForParser(searchName, sort, currentPage, parserProducts);
                    productService.findProducts(parserName, requestParam)
                            .doOnComplete(() -> {
                                if(count.decrementAndGet() == 0) {
                                    generateProductDto(fluxSink, sort);
                                }
                            })
                            .subscribe(parserProducts::add);
                });
            } else {
                generateProductDto(fluxSink, sort);
            }
        });
    }

    private ProductRequestParam getProductRequestParamForParser(String searchName, Sort sort, int currentPage, ParserProducts parserProducts) {
        ProductRequestParam requestParam = new ProductRequestParam();
        requestParam.setSearchName(searchName);
        int parserCurrentPage = currentPage == 0 ? 0 : parserProducts.getCurrentPage() + 24;
        requestParam.setPageNumber(parserCurrentPage);
        requestParam.setSort(sort);
        parserProducts.setCurrentPage(parserCurrentPage);
        return requestParam;
    }

    private void generateProductDto(FluxSink<ProductDto> fluxSink, Sort sort) {
        List<ProductDto> productDtos = getProducts(sort);
        productDtos.forEach(fluxSink::next);
        fluxSink.complete();
    }

    private List<ProductDto> getProducts(Sort sort) {
        Comparator<ProductDto> comparator = Comparator.comparing(productDto -> new BigDecimal(productDto.getPrice()));
        if(sort == Sort.price_desc) {
            comparator = comparator.reversed();
        }
        return productDtoMap.values()
                .stream()
                .flatMap(parserProducts -> parserProducts.getNextProducts().stream())
                .sorted(comparator)
                .limit(10)
                .peek(productDto -> productDtoMap.get(productDto.getShopName()).incrementIndex())
                .collect(Collectors.toList());
    }
}