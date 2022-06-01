package ru.manalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.manalyzer.IntegrationTestConfig;
import ru.manalyzer.diginetica.dto.DigineticaProductDto;
import ru.manalyzer.diginetica.dto.DigineticaResponseDto;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.parser.mvideo.service.MVideoHeadersService;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = IntegrationTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FavoriteControllerTest {

    private MockMvc mockMvc;

    @Autowired
    WebTestClient client;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private static ProductDto addingProduct;

    private static final ParameterizedTypeReference<ServerSentEvent<ProductDto>> typeRef =
            new ParameterizedTypeReference<>() {};

    private static MockWebServer mockWebServer;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void initClass() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(7777);

        addingProduct = new ProductDto();
        addingProduct.setId("1");
        addingProduct.setName("Macbook");
        addingProduct.setShopName("Oldi");
        addingProduct.setPrice("190000");
        addingProduct.setProductLink("https://www.oldi.ru/catalog/element/1");
        addingProduct.setImageLink("https://img.oldi.ru/");
    }

    @AfterAll
    public static void destroyWebServer() throws IOException {
        mockWebServer.close();
    }

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @WithMockUser(username = "admin@mail.ru", password = "admin")
    public void addProductToFavoritesCartTest() throws Exception {
        DigineticaProductDto digineticaProductDto = new DigineticaProductDto();
        digineticaProductDto.setId(addingProduct.getId());
        digineticaProductDto.setPrice(addingProduct.getPrice());
        digineticaProductDto.setName(addingProduct.getName());
        digineticaProductDto.setImage_url(addingProduct.getImageLink());
        digineticaProductDto.setLink_url("/catalog/element/1");
        digineticaProductDto.setScore(1.0);
        DigineticaResponseDto responseDto = new DigineticaResponseDto(1, List.of(digineticaProductDto));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));

        addProductToFavorite();

        List<ServerSentEvent<ProductDto>> productDtos = getProductDtos();

        ProductDto savedProductDto = productDtos.get(0).data();

        Assertions.assertEquals(addingProduct, savedProductDto);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @WithMockUser(username = "admin@mail.ru", password = "admin")
    public void deleteProductFromFavoritesCartTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/favorites/" + addingProduct.getId() + "/" + addingProduct.getShopName())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<ServerSentEvent<ProductDto>> productDtos = getProductDtos();
        Assertions.assertEquals(1, productDtos.size());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @WithMockUser(username = "admin@mail.ru", password = "admin")
    public void clearFavoritesCartTest() throws Exception {

        DigineticaProductDto digiProductDto = new DigineticaProductDto();
        digiProductDto.setId(addingProduct.getId());
        digiProductDto.setPrice(addingProduct.getPrice());
        digiProductDto.setName(addingProduct.getName());
        digiProductDto.setImage_url(addingProduct.getImageLink());
        digiProductDto.setLink_url("/catalog/element/1");
        digiProductDto.setScore(1.0);
        DigineticaResponseDto responseDto = new DigineticaResponseDto(1, List.of(digiProductDto));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mapper.writeValueAsString(responseDto))
                .addHeader("Content-Type", "application/json"));

        addProductToFavorite();
        addingProduct.setShopName("Citilink");
        addProductToFavorite();

        List<ServerSentEvent<ProductDto>> productDtos = getProductDtos();
        Assertions.assertEquals(2, productDtos.size());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/favorites")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        productDtos = getProductDtos();
        Assertions.assertEquals(1, productDtos.size());
    }

    private void addProductToFavorite() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/favorites")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(addingProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private List<ServerSentEvent<ProductDto>> getProductDtos() {
        return client
                .get()
                .uri("/favorites")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(typeRef)
                .getResponseBody()
                .collectList()
                .block();
    }
}
