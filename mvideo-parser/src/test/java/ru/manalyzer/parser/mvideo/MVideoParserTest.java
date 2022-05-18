package ru.manalyzer.parser.mvideo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;
import ru.manalyzer.parser.mvideo.config.MVideoProperties;
import ru.manalyzer.parser.mvideo.dto.*;
import ru.manalyzer.parser.mvideo.service.MVideoHeadersService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MVideoParserTest {

    private static final String propertyFile = "/mvideo-parser-test/test.yaml";
    private static MVideoProperties.Parser properties;

    private WebClient webClient;
    private ClientAndServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public void beforeAll() throws IOException {
        Yaml yaml = new Yaml(new Constructor(MVideoProperties.Parser.class));
        try(InputStream inputStream = getClass().getResourceAsStream(propertyFile)) {
            properties = yaml.load(inputStream);
        }
        String hostname = "http://localhost";
        Integer port = 8080;

        mockServer = ClientAndServer.startClientAndServer(port);
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
        List<String> productIds = getProductIds();
        List<ProductDto> expectedProducts = getExpectedProducts(productIds);

        String searchName = "холодильники и ноутбуки";

        HttpHeaders productIdsReqHeaders = getProductIdsReqHeaders();
        HttpHeaders productDetailsReqHeaders = getProductDetailsReqHeaders(productIdsReqHeaders, searchName);
        mockIdsRequest(productIds, searchName, productIdsReqHeaders);
        mockPriceRequest(expectedProducts, productIdsReqHeaders);
        mockProductDetailsRequest(expectedProducts, productDetailsReqHeaders);

        MVideoHeadersService mVideoHeadersService =
                getMockedMVideoHeadersService(productIdsReqHeaders, productDetailsReqHeaders, searchName);

        MVideoParser mVideoParser = new MVideoParser(webClient, mVideoHeadersService, properties);
        long start = System.currentTimeMillis();
        List<ProductDto> result = mVideoParser.parse(searchName, Sort.price_asc, 0)
                .collectList()
                .block();
        System.out.println("Время " + (System.currentTimeMillis() - start));
        assertNotNull(result);
        Map<String, ProductDto> resultProducts = result
                .stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));
        Map<String, ProductDto> expectedProductsMap = expectedProducts.stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        assertEquals(expectedProducts.size(), resultProducts.size());
        expectedProductsMap.values().forEach(productDto -> {
            ProductDto resultProduct = resultProducts.get(productDto.getId());
            assertNotNull(resultProduct);
            assertEquals(productDto.getName(), resultProduct.getName());
            assertEquals(productDto.getPrice(), resultProduct.getPrice());
            assertEquals(productDto.getImageLink(), resultProduct.getImageLink());
            assertEquals(productDto.getProductLink(), resultProduct.getProductLink());
            assertEquals(productDto.getShopName(), resultProduct.getShopName());
        });
    }

    private void mockIdsRequest(List<String> productIdsList,
                                String searchName,
                                HttpHeaders productIdsReqHeaders) {
        Map<String, List<String>> defaultParams = properties.getIdsRequest().getDefaultParams()
                .entrySet().stream()
                .map((entry) -> Map.entry(entry.getKey(), List.of(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        MVideoResponse<ProductIds> body = new MVideoResponse<>();
        ProductIds productIds = new ProductIds();
        productIds.setProducts(productIdsList);
        body.setBody(productIds);

        List<Header> reqHeaders = productIdsReqHeaders
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath(properties.getSearchUrl())
                        .withQueryStringParameter(properties.getIdsRequest().getSearchParamName(), searchName)
                        .withQueryStringParameters(defaultParams)
                        .withHeaders(reqHeaders))
                .respond(response()
                        .withBody(writeValueAsString(body))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private void mockPriceRequest(List<ProductDto> expectedProducts, HttpHeaders priceReqHeaders) {
        List<String> productIds = expectedProducts.stream().map(ProductDto::getId).collect(Collectors.toList());
        MVideoResponse<ProductPrices> body = new MVideoResponse<>();
        ProductPrices productPrices = new ProductPrices();
        List<MaterialPrice> materialPrices = expectedProducts.stream()
                .map(product -> {
                    MaterialPrice materialPrice = new MaterialPrice();
                    Price price = new Price();
                    price.setSalePrice(product.getPrice());
                    materialPrice.setPrice(price);
                    materialPrice.setProductId(product.getId());
                    return materialPrice;
                })
                .collect(Collectors.toList());
        productPrices.setMaterialPrices(materialPrices);
        body.setBody(productPrices);

        List<Header> reqHeaders = priceReqHeaders
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        mockServer
                .when(request()
                        .withMethod("GET").withPath(properties.getPriceUrl())
                        .withQueryStringParameter("productIds", String.join(",", productIds))
                        .withHeaders(reqHeaders))
                .respond(response()
                        .withBody(writeValueAsString(body))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private void mockProductDetailsRequest(List<ProductDto> expectedProducts, HttpHeaders detailsReqHeaders) {
        List<String> productIds = expectedProducts.stream().map(ProductDto::getId).collect(Collectors.toList());
        MVideoResponse<ProductDetails> body = new MVideoResponse<>();
        ProductDetails productDetails = new ProductDetails();
        List<ProductDetail> productDetailList = expectedProducts.stream()
                .map(productDto -> {
                    ProductDetail detail = new ProductDetail();
                    detail.setName(productDto.getName());
                    detail.setProductId(productDto.getId());
                    String image = productDto.getImageLink()
                            .substring(properties.getImageLinkPrefix().length());
                    detail.setImage(image);
                    String nameTranslit = productDto.getProductLink()
                            .substring(properties.getProductLinkPrefix().length());
                    nameTranslit = nameTranslit.substring(0, nameTranslit.length() - ("-" + productDto.getId()).length());
                    detail.setNameTranslit(nameTranslit);
                    return detail;
                })
                .collect(Collectors.toList());
        productDetails.setProducts(productDetailList);
        body.setBody(productDetails);

        List<Header> reqHeaders = detailsReqHeaders
                .entrySet()
                .stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        ProductListPostObject postObject = new ProductListPostObject(productIds);
        mockServer
                .when(request()
                        .withMethod("POST").withPath(properties.getProductDetailsUrl())
                        .withBody(writeValueAsString(postObject))
                        .withHeaders(reqHeaders))
                .respond(response()
                        .withBody(writeValueAsString(body))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private MVideoHeadersService getMockedMVideoHeadersService(HttpHeaders productsIdsHeaders,
                                                               HttpHeaders productPriceHeaders,
                                                               String searchName) {
        MVideoHeadersService mVideoHeadersService = mock(MVideoHeadersService.class);
        doReturn(productsIdsHeaders)
                .when(mVideoHeadersService)
                .getIdsHeaders();
        doReturn(productsIdsHeaders)
                .when(mVideoHeadersService)
                .getPriceHeaders();
        doReturn(productPriceHeaders)
                .when(mVideoHeadersService)
                .getDetailsHeaders(searchName);
        return mVideoHeadersService;
    }

    private List<String> getProductIds() {
        Random random = new Random();
        return IntStream.rangeClosed(0, 10)
                .mapToObj(value -> Integer.toString(random.nextInt(Integer.MAX_VALUE)))
                .collect(Collectors.toList());
    }

    private List<ProductDto> getExpectedProducts(List<String> productIds) {
        Random random = new Random();
        return productIds.stream()
                .map(productId -> {
                    ProductDto productDto = new ProductDto();
                    productDto.setId(productId);
                    productDto.setName(UUID.randomUUID().toString());
                    productDto.setPrice(Integer.toString(random.nextInt(Integer.MAX_VALUE)));
                    productDto.setShopName(properties.getShopName());
                    productDto.setProductLink(properties.getProductLinkPrefix() + UUID.randomUUID() + "-" + productId);
                    productDto.setImageLink(properties.getImageLinkPrefix() + UUID.randomUUID());
                    return productDto;
                })
                .collect(Collectors.toList());
    }

    private HttpHeaders getProductIdsReqHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.HOST, "www.mvideo.ru");
        headers.add(HttpHeaders.USER_AGENT, "Mozilla");
        headers.add(HttpHeaders.COOKIE, "JSESSIONID=" + UUID.randomUUID());
        return headers;
    }

    private HttpHeaders getProductDetailsReqHeaders(HttpHeaders commonHeaders, String searchName) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(commonHeaders);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String referer = UriComponentsBuilder
                .fromPath("https://www.mvideo.ru/product-list-page")
                .queryParam("q", searchName)
                .encode().build().toString();
        headers.add(HttpHeaders.REFERER, referer);
        return headers;
    }

    private String writeValueAsString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("");
        }
    }
}
