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

    private static Flux<ProductDto> oldiFlux;

    private static Flux<ProductDto> mvideoFlux;

    @BeforeAll
    public static void initClass() {
        oldiParser = mock(Parser.class);
        mvideoParser = mock(Parser.class);
        productService = new ProductServiceImpl(List.of(mvideoParser, oldiParser));

        oldiFlux = Flux.just(
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

        mvideoFlux = Flux.just(
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
    }

    @Test
    public void findProductsTest() {
        String searchName = "macbook";
        ProductRequestParam requestParam = new ProductRequestParam(searchName);

        when(oldiParser.parse(requestParam.getSearchName())).thenReturn(oldiFlux);
        when(mvideoParser.parse(requestParam.getSearchName())).thenReturn(mvideoFlux);

        Flux<ProductDto> productDtoFlux = productService.findProducts(requestParam);

        StepVerifier.create(productDtoFlux)
                .expectSubscription()
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .expectComplete()
                .verify();
    }

    @Test
    public void findProductsOnBackpressureTest() {
        String searchName = "macbook";
        ProductRequestParam requestParam = new ProductRequestParam(searchName);

        when(oldiParser.parse(requestParam.getSearchName())).thenReturn(oldiFlux);
        when(mvideoParser.parse(requestParam.getSearchName())).thenReturn(mvideoFlux);

        Flux<ProductDto> productDtoFlux = productService.findProducts(requestParam);

        StepVerifier.create(productDtoFlux.onBackpressureBuffer(), 0)
                .expectSubscription()
                .thenRequest(1)
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .thenRequest(1)
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .thenRequest(1)
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .thenRequest(1)
                .assertNext(dto -> MatcherAssert.assertThat(dto.getShopName(), Matchers.in(List.of("Oldi", "M.Video"))))
                .expectComplete()
                .verify();
    }

}
