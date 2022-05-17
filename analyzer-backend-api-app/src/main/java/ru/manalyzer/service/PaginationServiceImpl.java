package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import reactor.core.publisher.Flux;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;

import java.util.Optional;

@Service
@SessionScope
public class PaginationServiceImpl implements PaginationService {

    private final ParserProductsStorage parserProductsStorage;

    private final ProductRequestParam currentRequestParam =
            new ProductRequestParam("", Optional.of("0"), Optional.of(Sort.price_asc));

    @Autowired
    public PaginationServiceImpl(ParserProductsStorage parserProductsStorage) {
        this.parserProductsStorage = parserProductsStorage;
    }

    @Override
    public Flux<ProductDto> findProducts(ProductRequestParam requestParam) {
        String requestedSearchName = requestParam.getSearchName();
        String currentSearchName = currentRequestParam.getSearchName();
        int requestedPage = Integer.parseInt(requestParam.getPageNumber().orElse("0"));
        int currentPage = Integer.parseInt(currentRequestParam.getPageNumber().orElse("0"));
        Sort requestedSort = requestParam.getSort().orElse(Sort.price_asc);
        Sort currentSort = currentRequestParam.getSort().orElse(Sort.price_asc);
        if(!currentSearchName.equals(requestedSearchName) || requestedPage <= currentPage || requestedSort != currentSort) {
            parserProductsStorage.clear();
        }
        currentRequestParam.setSearchName(requestedSearchName);
        currentRequestParam.setPageNumber(requestParam.getPageNumber());
        currentRequestParam.setSort(Optional.of(requestedSort));
        return parserProductsStorage.getProducts(requestedSearchName, requestedSort, requestedPage);
    }
}