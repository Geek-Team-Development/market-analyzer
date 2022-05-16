package ru.manalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.service.PaginationService;
import ru.manalyzer.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final PaginationService paginationService;

    @Autowired
    public ProductController(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    @GetMapping(produces = "text/event-stream")
    public Flux<ProductDto> findProducts(ProductRequestParam requestParam) {
        return paginationService.findProducts(requestParam);
    }
}
