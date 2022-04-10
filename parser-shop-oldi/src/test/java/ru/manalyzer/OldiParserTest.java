package ru.manalyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.manalyzer.dto.*;
import ru.manalyzer.property.OldiConnectionProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class OldiParserTest {

    private static MockWebServer mockWebServer;

    private static ObjectMapper mapper;

    private Parser oldiParser;

    @BeforeAll
    public static void initWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    public static void destroyWebServer() throws IOException {
        mockWebServer.close();
    }

    @BeforeEach
    public void init() {
        OldiConnectionProperties connectionProperties = new OldiConnectionProperties(
                "Oldi",
                "https://www.oldi.ru",
                String.format("http://localhost:%s", mockWebServer.getPort()),
                "",
                "st",
                new HashMap<>()
        );
        ConverterDto converterDto =
                new ConverterOldiDtoToDto(connectionProperties.getShopName(), connectionProperties.getShopUri());

        mapper = new ObjectMapper();
        oldiParser = new OldiParser(connectionProperties, converterDto);
    }

    @Test
    public void findProductsTest() throws JsonProcessingException {
        OldiProductDto oldiProductDto = new OldiProductDto(
                "1",
                "MacBook Pro",
                "200000",
                1.0,
                "/catalog/element/1",
                "https://img.oldi.ru/"
        );

        OldiResponseDto responseDto = new OldiResponseDto(1, List.of(oldiProductDto));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));

        Flux<ProductDto> products = oldiParser.parse("macbook");

        ProductDto productDto = new ProductDto(
                "1",
                "MacBook Pro",
                "200000",
                "https://www.oldi.ru/catalog/element/1",
                "https://img.oldi.ru/",
                "Oldi"
        );

        StepVerifier.create(products)
                .expectSubscription()
                .expectNextMatches(productDto::equals)
                .expectComplete()
                .verify();
    }

    @Test
    public void errorFindProductsTest() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(mapper.writeValueAsString(new OldiResponseDto()))
                .addHeader("Content-Type", "application/json"));

        Flux<ProductDto> products = oldiParser.parse("macbook");

        StepVerifier.create(products)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    public void findProductsOnBackpressureTest() throws JsonProcessingException {
        OldiProductDto oldiProductDto1 = new OldiProductDto(
                "1",
                "MacBook Pro",
                "200000",
                1.0,
                "/catalog/element/1",
                "https://img.oldi.ru/"
        );

        OldiProductDto oldiProductDto2 = new OldiProductDto(
                "1",
                "MacBook Air",
                "150000",
                1.0,
                "/catalog/element/2",
                "https://img.oldi.ru/"
        );


        OldiResponseDto responseDto =
                new OldiResponseDto(1, List.of(oldiProductDto1, oldiProductDto2));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));

        Flux<ProductDto> products = oldiParser.parse("macbook");

        StepVerifier.create(products.onBackpressureBuffer(), 0)
                .expectSubscription()
                .thenRequest(1)
                .expectNextMatches(dto -> dto.getName().toLowerCase().contains("macbook"))
                .thenRequest(1)
                .expectNextMatches(dto -> dto.getName().toLowerCase().contains("macbook"))
                .expectComplete()
                .verify();
    }

}
