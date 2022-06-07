package ru.manalyzer.parser.mvideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;
import ru.manalyzer.parser.mvideo.config.properties.ParserProperties;
import ru.manalyzer.parser.mvideo.dto.*;
import ru.manalyzer.parser.mvideo.service.HeadersService;

import java.util.*;

/**
 * Парсер информации о продуктах с сервера MVideo
 */
@Service
public class MVideoParser implements ru.manalyzer.Parser {

    private final HeadersService headersService;
    private final WebClient webClient;
    private final ParserProperties properties;

    private static final Logger logger = LoggerFactory.getLogger(MVideoParser.class);

    @Autowired
    public MVideoParser(WebClient webClient,
                        HeadersService headersService,
                        ParserProperties properties) {
        this.webClient = webClient;
        this.headersService = headersService;
        this.properties = properties;
    }

    public Flux<ProductDto> parse(String searchName, Sort sort, int pageNumber) {
        logger.info("Products requested with name {}", searchName);
        return getProductIds(searchName, sort, pageNumber).flatMapMany(productIds -> {
            if (productIds.size() == 0) {
                return Flux.empty();
            }

            return createProductDtoFlux(productIds, searchName);
        });
    }

    private Flux<ProductDto> createProductDtoFlux(List<String> productIds, String searchName) {
        Mono<List<MaterialPrice>> prices = getProductPrice(productIds);
        Mono<List<ProductDetail>> details = getProductDetails(productIds, searchName);
        Map<String, ProductDto> productMap = initProductMap(productIds);

        return Mono.zip(prices, details)
                .flatMapIterable(tuple -> {
                    setProductDetails(productMap, tuple.getT2());
                    setProductPrices(productMap, tuple.getT1());
                    return productMap.values();
                });
    }

    private Mono<List<String>> getProductIds(String searchName, Sort sort, int pageNumber) {
        HttpHeaders productIdsRequestHeaders = headersService.getIdsHeaders();
        return webClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path(properties.getSearchUrl());
                    Map<String, String> params = getParamsForIdsRequest(searchName, sort, pageNumber);
                    params.forEach(uriBuilder::queryParam);
                    Map<String, List<String>> filterParams = getFilterParams();
                    filterParams.forEach((filterParamName, filterParamValues) ->
                            filterParamValues.forEach(filterParamValue ->
                                    uriBuilder.queryParam(filterParamName, "{filterParams}")));
                    String filterParamName = properties.getIdsRequest().getFilterParamName();
                    List<String> filterParamValuesB64 = filterParams.get(filterParamName);
                    return uriBuilder.build(filterParamValuesB64.toArray());
                })
                .headers(httpHeaders -> httpHeaders.addAll(productIdsRequestHeaders))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MVideoResponse<ProductIds>>() {
                })
                .map(productIds -> productIds.getBody().getProducts())
                .onErrorReturn(List.of());
    }

    private Map<String, String> getParamsForIdsRequest(String searchName, Sort sort, int pageNumber) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(properties.getIdsRequest().getSearchParamName(), searchName);
        result.put(properties.getIdsRequest().getOffsetParamName(), Integer.toString(pageNumber));
        result.put(properties.getIdsRequest().getSortParamName(), sort.toString());
        result.putAll(properties.getIdsRequest().getDefaultParams());
        return result;
    }

    private Map<String, List<String>> getFilterParams() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        String filterParamName = properties.getIdsRequest().getFilterParamName();
        List<String> filterParamKeys = properties.getIdsRequest().getFilterParamNames();
        List<String> filterParamValuesB64 = new ArrayList<>();
        filterParamKeys.forEach(filterParamKey -> {
            String filterParamValue = properties.getIdsRequest().getFilterParams().get(filterParamKey);
            String filterParamValueB64 = Base64.getEncoder().encodeToString(filterParamValue.getBytes());
            filterParamValuesB64.add(filterParamValueB64);
        });
        result.put(filterParamName, filterParamValuesB64);
        return result;
    }

    private Mono<List<MaterialPrice>> getProductPrice(List<String> productIds) {
        String productIdsStr = String.join(",", productIds);
        HttpHeaders productIdsRequestHeaders = headersService.getIdsHeaders();
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getPriceUrl())
                        .queryParam("productIds", productIdsStr)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(productIdsRequestHeaders))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MVideoResponse<ProductPrices>>() {
                })
                .map(productPricesMVideoResponse -> productPricesMVideoResponse.getBody().getMaterialPrices());
    }

    private Mono<List<ProductDetail>> getProductDetails(List<String> productIds,
                                                        String searchName) {
        ProductListPostObject postObject = new ProductListPostObject(productIds);
        HttpHeaders productDetailsRequestHeaders = headersService.getDetailsHeaders(searchName);
        return webClient
                .post()
                .uri(properties.getProductDetailsUrl())
                .headers(httpHeaders -> httpHeaders.addAll(productDetailsRequestHeaders))
                .bodyValue(postObject)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MVideoResponse<ProductDetails>>() {
                })
                .map(productDetailsMVideoResponse -> productDetailsMVideoResponse.getBody().getProducts());
    }

    private Map<String, ProductDto> initProductMap(List<String> productIds) {
        Map<String, ProductDto> productMap = new LinkedHashMap<>();
        productIds.forEach(productId -> {
            ProductDto productDto = new ProductDto();
            productDto.setId(productId);
            productDto.setShopName(properties.getShopName());
            productMap.put(productId, productDto);
        });
        return productMap;
    }

    private void setProductDetails(Map<String, ProductDto> productMap, List<ProductDetail> productDetails) {
        productDetails.forEach(productDetail -> {
            String id = productDetail.getProductId();
            ProductDto productDto = productMap.get(id);
            productDto.setName(productDetail.getName());
            productDto.setProductLink(properties.getProductLinkPrefix() + productDetail.getNameTranslit() + "-" + id);
            productDto.setImageLink(properties.getImageLinkPrefix() + productDetail.getImage());
        });
    }

    private void setProductPrices(Map<String, ProductDto> productMap, List<MaterialPrice> prices) {
        prices.forEach(materialPrice -> {
            ProductDto productDto = productMap.get(materialPrice.getProductId());
            String salePrice = materialPrice.getPrice().getSalePrice();
            String basePrice = materialPrice.getPrice().getBasePrice();
            if(salePrice == null && basePrice == null) {
                productMap.remove(materialPrice.getProductId());
                return;
            }
            String price = salePrice;
            if(price == null) {
                price = materialPrice.getPrice().getBasePrice();
            }
            productDto.setPrice(price);
        });
    }

    @Override
    public Mono<ProductDto> parseOneProduct(ProductDto productDto) {
        return Mono.from(createProductDtoFlux(List.of(productDto.getId()), productDto.getName()));
    }

    @Override
    public Mono<ProductDto> parseProductPage(ProductDto productDto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getShopName() {
        return properties.getShopName();
    }
}
