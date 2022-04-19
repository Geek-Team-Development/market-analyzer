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
import ru.manalyzer.diginetica.dto.ConverterDigineticaDtoToDto;
import ru.manalyzer.diginetica.dto.DigineticaProductDto;
import ru.manalyzer.diginetica.dto.DigineticaResponseDto;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.parsers.citilink.CitilinkConnectionProperties;
import ru.manalyzer.parsers.citilink.CitilinkParser;
import ru.manalyzer.parsers.citilink.ConverterCitilinkDtoToDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CitilinkParserTest {

    private static MockWebServer mockWebServer;

    private static ObjectMapper mapper;

    private Parser citilinkParser;

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
        DigineticaConnectionProperties connectionProperties = new DigineticaConnectionProperties(
                "Citilink",
                "https://www.citilink.ru",
                String.format("http://localhost:%s", mockWebServer.getPort()),
                "",
                "st",
                new HashMap<>()
        );
        ConverterDigineticaDtoToDto converterDto =
                new ConverterDigineticaDtoToDto(connectionProperties);
        mapper = new ObjectMapper();
        citilinkParser = new CitilinkParser(connectionProperties, converterDto);
    }

    @Test
    public void findProductsTest() throws JsonProcessingException {
        DigineticaProductDto digineticaProductDto = new DigineticaProductDto(
                "1",
                "MacBook Pro",
                "200000",
                1.0,
                "/catalog/element/1",
                "https://img.citilink.ru/"
        );

        DigineticaResponseDto responseDto = new DigineticaResponseDto(1, List.of(digineticaProductDto));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));

        Flux<ProductDto> products = citilinkParser.parse("macbook");

        ProductDto productDto = new ProductDto(
                "1",
                "MacBook Pro",
                "200000",
                "https://www.citilink.ru/catalog/element/1",
                "https://img.citilink.ru/",
                "Citilink"
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
                .setBody(mapper.writeValueAsString(new DigineticaResponseDto()))
                .addHeader("Content-Type", "application/json"));

        Flux<ProductDto> products = citilinkParser.parse("macbook");

        StepVerifier.create(products)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    public void findProductsOnBackpressureTest() throws JsonProcessingException {
        DigineticaProductDto digineticaProductDto1 = new DigineticaProductDto(
                "1",
                "MacBook Pro",
                "200000",
                1.0,
                "/catalog/element/1",
                "https://img.citilink.ru/"
        );

        DigineticaProductDto digineticaProductDto2 = new DigineticaProductDto(
                "2",
                "MacBook Air",
                "150000",
                1.0,
                "/catalog/element/2",
                "https://img.citilink.ru/"
        );


        DigineticaResponseDto responseDto =
                new DigineticaResponseDto(2, List.of(digineticaProductDto1, digineticaProductDto2));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));

        Flux<ProductDto> products = citilinkParser.parse("macbook");

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
