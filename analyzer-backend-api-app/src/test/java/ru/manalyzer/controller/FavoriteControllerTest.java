package ru.manalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.manalyzer.AnalyzerBackendApiAppApplication;
import ru.manalyzer.config.WebSocketSecurityConfiguration;
import ru.manalyzer.dto.ProductDto;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = AnalyzerBackendApiAppApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringJUnitConfig
public class FavoriteControllerTest {

    private MockMvc mockMvc;

    @Autowired
    WebTestClient client;

    @MockBean
    AmqpAdmin amqpAdmin;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private static ProductDto addingProduct;

    private static final ParameterizedTypeReference<ServerSentEvent<ProductDto>> typeRef =
            new ParameterizedTypeReference<>() {};

    @BeforeAll
    public static void initClass() {
        addingProduct = new ProductDto();
        addingProduct.setId("1");
        addingProduct.setName("Macbook");
        addingProduct.setShopName("Oldi");
        addingProduct.setPrice("190000");
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

    @Autowired
    ApplicationContext ctx;

    @Test
    @org.junit.jupiter.api.Order(1)
    @WithMockUser(username = "admin@mail.ru", password = "admin")
    public void addProductToFavoritesCartTest() throws Exception {
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
        addProductToFavorite();
        addingProduct.setShopName("M.Video");
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
