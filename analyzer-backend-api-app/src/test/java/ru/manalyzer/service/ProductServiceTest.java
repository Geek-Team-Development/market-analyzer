package ru.manalyzer.service;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.manalyzer.Parser;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    private static ProductService productService;

    private static Parser oldiParser;

    private static Parser mvideoParser;

    @BeforeAll
    public static void initClass() {
        oldiParser = mock(Parser.class);
        mvideoParser = mock(Parser.class);
        productService = new ProductServiceImpl(List.of(mvideoParser, oldiParser));
    }

    @Test
    public void findProductsTest() {
        String searchName = "macbook";

        Flux<ProductDto> oldiFlux = Flux.just(
                new ProductDto("1",
                        "Macbook Pro",
                        "200000",
                        "https://www.oldi.ru/catalog/element/1",
                        "https://img.oldi.ru/",
                        "Oldi"
                ),
                new ProductDto("2",
                        "Macbook Air",
                        "150000",
                        "https://www.oldi.ru/catalog/element/2",
                        "https://img.oldi.ru/",
                        "Oldi"
                )
        );

        Flux<ProductDto> mvideoFlux = Flux.just(
                new ProductDto("1",
                        "Macbook Pro",
                        "190000",
                        "https://www.mvideo.ru/catalog/element/1",
                        "https://img.mvideo.ru/",
                        "M.Video"
                ),
                new ProductDto("2",
                        "Macbook Air",
                        "140000",
                        "https://www.mvideo.ru/catalog/element/2",
                        "https://img.mvideo.ru/",
                        "M.Video"
                )
        );

        when(oldiParser.parse(searchName)).thenReturn(oldiFlux);
        when(mvideoParser.parse(searchName)).thenReturn(mvideoFlux);

        Flux<ProductDto> productDtoFlux = productService.findProducts(new ProductRequestParam(searchName));

        StepVerifier.create(productDtoFlux)
                .expectSubscription()
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .expectComplete()
                .verify();
    }

}
