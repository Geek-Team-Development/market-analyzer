package ru.manalyzer.parser.mvideo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.parser.mvideo.dto.*;
import ru.manalyzer.parser.mvideo.service.MVideoHeadersService;
import ru.manalyzer.parser.mvideo.utils.TestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockserver.configuration.Configuration.configuration;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoParserTest {

    private static final String PROPERTY_FILE = "/mvideo-parser-test/test.properties";

    private Properties properties;
    private WebClient webClient;
    private ClientAndServer mockServer;

    private String hostname;
    private Integer port;

    @BeforeAll
    public void beforeAll() {
        properties = TestUtils.loadProperties(PROPERTY_FILE);
        hostname = properties.getProperty("mvideo.main.url");
        port = Integer.parseInt(properties.getProperty("mvideo.server.port"));
        mockServer = ClientAndServer.startClientAndServer(configuration()
                .disableLogging(false), port);
        webClient = WebClient.builder()
                .baseUrl(hostname + ":" + port)
                .build();
    }

    @AfterAll
    public void afterAll() {
        mockServer.stop();
    }

    @Test
    public void getProductsTest() {

        ResponseEntity<MVideoResponse<ProductIds>> responseProductIds = TestUtils.readFromFile(
                properties.getProperty("mvideo.product-ids-response.file"),
                new TypeReference<ResponseEntity<MVideoResponse<ProductIds>>>() { });

        ResponseEntity<MVideoResponse<ProductPrices>> responseProductPrices = TestUtils.readFromFile(
                properties.getProperty("mvideo.product-prices-response.file"),
                new TypeReference<ResponseEntity<MVideoResponse<ProductPrices>>>() { });

        ResponseEntity<MVideoResponse<ProductDetails>> responseProductDetails = TestUtils.readFromFile(
                properties.getProperty("mvideo.product-details-response.file"),
                new TypeReference<ResponseEntity<MVideoResponse<ProductDetails>>>() { });

        HttpHeaders productIdsHeaders = TestUtils.readFromFile(properties.getProperty("mvideo.common-headers.file"),  HttpHeaders.class);
        HttpHeaders productDetailsHeaders = TestUtils.readFromFile(properties.getProperty("mvideo.product-details-headers.file"),  HttpHeaders.class);
        List<String> productIds = responseProductIds.getBody().getBody().getProducts();
        mockIdsRequest(responseProductIds);
        mockPriceRequest(responseProductPrices, productIds);
        mockProductDetailsRequest(responseProductDetails, productIds);

        String searchName = "холодильники и ноутбуки";
        MVideoHeadersService mVideoHeadersService = getMockedMVideoHeadersService(productIdsHeaders, productDetailsHeaders, searchName);
        MVideoParser mVideoParser = new MVideoParser(webClient, mVideoHeadersService);
        TestUtils.setPrivateFields(mVideoParser, properties);
        long start = System.currentTimeMillis();
        List<ProductDto> result = mVideoParser.parse(searchName)
                .collectList()
                .block();
        System.out.println("Время " + (System.currentTimeMillis() - start));
        Map<String, ProductDto> resultProducts = result
                .stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));
        Map<String, ProductDto> expectedProducts =
                getExpectedProducts(responseProductIds,
                        responseProductPrices,
                        responseProductDetails)
                        .stream()
                        .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        assertEquals(expectedProducts.size(), resultProducts.size());
        expectedProducts.values().forEach(productDto -> {
            ProductDto resultProduct = resultProducts.get(productDto.getId());
            assertNotNull(resultProduct);
            assertEquals(productDto.getName(), resultProduct.getName());
            assertEquals(productDto.getPrice(), resultProduct.getPrice());
            assertEquals(productDto.getImageLink(), resultProduct.getImageLink());
            assertEquals(productDto.getProductLink(), resultProduct.getProductLink());
            assertEquals(productDto.getShopName(), resultProduct.getShopName());
        });
    }

    private void mockIdsRequest(ResponseEntity<MVideoResponse<ProductIds>> responseProductIds) {
        List<Header> headersProductIds = responseProductIds
                .getHeaders()
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        mockServer
                .when(request()
                        .withMethod("GET").withPath(properties.getProperty("mvideo.search-url")))
                .respond(response()
                        .withBody(TestUtils.writeAsString(responseProductIds.getBody()))
                        .withHeaders(headersProductIds));
    }

    private void mockPriceRequest(ResponseEntity<MVideoResponse<ProductPrices>> responseProductPrices,
                                  List<String> productIds) {
        List<Header> headersProductPrices = responseProductPrices
                .getHeaders()
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        mockServer
                .when(request()
                        .withMethod("GET").withPath(properties.getProperty("mvideo.price-url"))
                        .withQueryStringParameter("productIds", String.join(",", productIds)))
                .respond(response()
                        .withBody(TestUtils.writeAsString(responseProductPrices.getBody()))
                        .withHeaders(headersProductPrices));
    }

    private void mockProductDetailsRequest(ResponseEntity<MVideoResponse<ProductDetails>> responseProductDetails,
                                           List<String> productIds) {
        List<Header> headersProductDetails = responseProductDetails
                .getHeaders()
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductListPostObject postObject = new ProductListPostObject(productIds);
            mockServer
                    .when(request()
                            .withMethod("POST").withPath(properties.getProperty("mvideo.product-details.url"))
                            .withBody(mapper.writeValueAsString(postObject), StandardCharsets.UTF_8))
                    .respond(response()
                            .withBody(TestUtils.writeAsString(responseProductDetails.getBody()))
                            .withHeaders(headersProductDetails));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private List<ProductDto> getExpectedProducts(ResponseEntity<MVideoResponse<ProductIds>> responseProductIds,
                                                 ResponseEntity<MVideoResponse<ProductPrices>> responseProductPrices,
                                                 ResponseEntity<MVideoResponse<ProductDetails>> responseProductDetails) {
        List<String> productIds = responseProductIds.getBody().getBody().getProducts();
        Map<String, MaterialPrice> productPrices = responseProductPrices.getBody().getBody().getMaterialPrices()
                .stream().collect(Collectors.toMap(MaterialPrice::getProductId, Function.identity()));
        Map<String, ProductDetail> productDetailMap = responseProductDetails.getBody().getBody()
                .getProducts().stream()
                .collect(Collectors.toMap(ProductDetail::getProductId, Function.identity()));
        Map<String, ProductDto> productMap = new HashMap<>();
        productIds.forEach(productId -> {
            ProductDto product = new ProductDto();
            product.setId(productId);
            product.setPrice(productPrices.get(productId).getPrice().getBasePrice());
            ProductDetail productDetail = productDetailMap.get(productId);
            product.setName(productDetail.getName());
            product.setImageLink("https://img.mvideo.ru/" + productDetail.getImage());
            product.setProductLink("https://www.mvideo.ru/products/" + productDetail.getNameTranslit() + "-" + productId);
            product.setShopName("MVideo");
            productMap.put(productId, product);
        });
        return new ArrayList<>(productMap.values());
    }

    private MVideoHeadersService getMockedMVideoHeadersService(HttpHeaders productIdsHeaders,
                                                               HttpHeaders productDetailsHeaders,
                                                               String searchName) {
        MVideoHeadersService mVideoHeadersService = mock(MVideoHeadersService.class);
        doReturn(productIdsHeaders)
                .when(mVideoHeadersService)
                .getIdsHeaders();
        doReturn(productDetailsHeaders)
                .when(mVideoHeadersService)
                .getDetailsHeaders(searchName);
        return mVideoHeadersService;
    }
}
