package ru.manalyzer.controller;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.manalyzer.IntegrationTestConfig;
import ru.manalyzer.dto.ProductDto;

import java.util.List;

@SpringBootTest(
        classes = IntegrationTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ProductControllerIntegrationTest {

    @Autowired
    WebTestClient client;

    private static final ParameterizedTypeReference<ServerSentEvent<ProductDto>> typeRef =
            new ParameterizedTypeReference<>() {};

    @Test
    public void findProductsTest() {
        String searchName = "macbook";

        List<ServerSentEvent<ProductDto>> events = client
                .get()
                .uri("/product?searchName=" + searchName)
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(typeRef)
                .getResponseBody()
                .collectList()
                .block();

        events.forEach(event -> {
            MatcherAssert.assertThat(event.data().getName(), Matchers.containsStringIgnoringCase(searchName));
            MatcherAssert.assertThat(event.data().getId(), Matchers.not(Matchers.blankOrNullString()));
            MatcherAssert.assertThat(event.data().getShopName(), Matchers.not(Matchers.blankOrNullString()));
            MatcherAssert.assertThat(event.data().getProductLink(), Matchers.not(Matchers.blankOrNullString()));
            MatcherAssert.assertThat(event.data().getImageLink(), Matchers.not(Matchers.blankOrNullString()));
            MatcherAssert.assertThat(event.data().getPrice(), Matchers.not(Matchers.blankOrNullString()));
        });
    }
}
