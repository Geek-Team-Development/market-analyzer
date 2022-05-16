package ru.manalyzer.controller;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.manalyzer.controller.param.ProductRequestParam;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;
import ru.manalyzer.service.ProductService;
import ru.manalyzer.service.ProductServiceImpl;

import java.util.Optional;


public class ProductControllerTest {

    private static ProductService productService;

    private static ProductController productController;

    private static Flux<ProductDto> productDtoFlux;

    private static final ParameterizedTypeReference<ServerSentEvent<ProductDto>> typeRef =
            new ParameterizedTypeReference<>() {};

    @BeforeAll
    public static void initAll() {
        productService = Mockito.mock(ProductServiceImpl.class);
//        productController = new ProductController(productService);

        productDtoFlux = Flux.create(sink -> {
                        sink.next(new ProductDto(
                                "1",
                                "Macbook Pro",
                                "200000",
                                "https://www.oldi.ru/catalog/element/1",
                                "https://img.oldi.ru/",
                                "Oldi"
                        ));
                        sink.next(new ProductDto(
                                "2",
                                "Macbook Air",
                                "150000",
                                "https://www.oldi.ru/catalog/element/2",
                                "https://img.oldi.ru/",
                                "Oldi"
                        ));
                        sink.next(new ProductDto(
                                "1",
                                "Macbook Pro",
                                "190000",
                                "https://www.mvideo.ru/catalog/element/1",
                                "https://img.mvideo.ru/",
                                "M.Video"
                        ));
                        sink.next(new ProductDto(
                                "2",
                                "Macbook Air",
                                "140000",
                                "https://www.mvideo.ru/catalog/element/2",
                                "https://img.mvideo.ru/",
                                "M.Video"
                        ));
                        sink.complete();
                }
        );
    }

    @Test
    public void findProductsTest() {
        String searchName = "macbook";
        ProductRequestParam requestParam = new ProductRequestParam(searchName, Optional.of("0"), Optional.of(Sort.price_asc));

        Mockito.when(productService.findProducts(requestParam))
                .thenReturn(productDtoFlux);

        WebTestClient
                .bindToController(productController)
                .build()
                .get()
                .uri("/product?searchName=macbook")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(typeRef)
                .getResponseBody()
                .as(StepVerifier::create)
                .expectSubscription()
                .assertNext(dto ->
                        MatcherAssert.assertThat(dto.data().getName(), Matchers.containsStringIgnoringCase(searchName)))
                .assertNext(dto ->
                        MatcherAssert.assertThat(dto.data().getName(), Matchers.containsStringIgnoringCase(searchName)))
                .assertNext(dto ->
                        MatcherAssert.assertThat(dto.data().getName(), Matchers.containsStringIgnoringCase(searchName)))
                .assertNext(dto ->
                        MatcherAssert.assertThat(dto.data().getName(), Matchers.containsStringIgnoringCase(searchName)))
                .expectComplete()
                .verify();

    }
}
