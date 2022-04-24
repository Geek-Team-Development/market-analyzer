package ru.manalyzer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import ru.manalyzer.AnalyzerBackendApiAppApplication;
import ru.manalyzer.dto.ProductDto;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = AnalyzerBackendApiAppApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class FavoriteControllerTest {

    private MockMvc mockMvc;

    @Autowired
    WebTestClient client;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private static ProductDto addingProduct;

    @BeforeAll
    public static void initClass() {
        addingProduct = new ProductDto();
        addingProduct.setId("1");
        addingProduct.setName("Macbook");
        addingProduct.setShopName("MVideo");
        addingProduct.setPrice("200000");
        addingProduct.setProductLink("");
        addingProduct.setImageLink("");
    }

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin@mail.ru", password = "admin")
    public void addProductToFavoritesCartTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/favorites")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(addingProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<ProductDto> productDtos = client
                .get()
                .uri("/favorites")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(ProductDto.class)
                .getResponseBody()
                .collectList()
                .block();

        ProductDto savedProductDto = productDtos.get(0);

        Assertions.assertEquals(addingProduct, savedProductDto);
    }
}
