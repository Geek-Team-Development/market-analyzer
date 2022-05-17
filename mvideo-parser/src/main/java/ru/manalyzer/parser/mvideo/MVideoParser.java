package ru.manalyzer.parser.mvideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;
import ru.manalyzer.parser.mvideo.config.MVideoProperties;
import ru.manalyzer.parser.mvideo.dto.*;
import ru.manalyzer.parser.mvideo.service.MVideoHeadersService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Парсер информации о продуктах с сервера MVideo
 */
@Service
public class MVideoParser implements Parser {

    private final MVideoHeadersService mVideoHeadersService;
    private final WebClient webClient;
    private final MVideoProperties.Parser properties;

    private static final Logger logger = LoggerFactory.getLogger(MVideoParser.class);

    @Autowired
    public MVideoParser(WebClient webClient,
                        MVideoHeadersService mVideoHeadersService,
                        MVideoProperties.Parser properties) {
        this.webClient = webClient;
        this.mVideoHeadersService = mVideoHeadersService;
        this.properties = properties;
    }

    public Flux<ProductDto> parse(String searchName, Sort sort, String pageNumber) {
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

        Map<String, ProductDto> map = new LinkedHashMap<>();
        productIds.forEach(productId -> {
            ProductDto productDto = new ProductDto();
            productDto.setId(productId);
            productDto.setShopName("MVideo");
            map.put(productId, productDto);
        });

        return Mono.zip(prices, details)
                .flatMapIterable(tuple -> {
                    tuple.getT1()
                            .forEach(materialPrice -> {
                                ProductDto productDto = map.get(materialPrice.getProductId());
                                String price = materialPrice.getPrice().getSalePrice();
                                if(price == null) {
                                    price = materialPrice.getPrice().getBasePrice();
                                }
                                productDto.setPrice(price);
                            });
                    tuple.getT2()
                            .forEach(productDetail -> {
                                String id = productDetail.getProductId();
                                ProductDto productDto = map.get(id);
                                productDto.setName(productDetail.getName());
                                productDto.setProductLink(properties.getProductLinkPrefix() + productDetail.getNameTranslit() + "-" + id);
                                productDto.setImageLink(properties.getImageLinkPrefix() + productDetail.getImage());
                            });
                    return map.values();
                });
    }

    private Mono<List<String>> getProductIds(String searchName, Sort sort, String pageNumber) {
        HttpHeaders productIdsRequestHeaders = mVideoHeadersService.getIdsHeaders();
        return webClient
                .get()
                .uri(uriBuilder -> {
                    String searchParamName = properties.getIdsRequest().getSearchParamName();
                    String sortParamName = properties.getIdsRequest().getSortParamName();
                    String offsetParamName = properties.getIdsRequest().getOffsetParamName();
                    uriBuilder
                            .path(properties.getSearchUrl())
                            .queryParam(searchParamName, searchName)
                            .queryParam(offsetParamName, pageNumber)
                            .queryParam(sortParamName, sort.toString());
                    Map<String, String> defaultParams = properties.getIdsRequest().getDefaultParams();
                    defaultParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .headers(httpHeaders -> httpHeaders.addAll(productIdsRequestHeaders))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MVideoResponse<ProductIds>>() {
                })
                .map(productIds -> productIds.getBody().getProducts())
                .onErrorReturn(List.of());
    }

    private Mono<List<MaterialPrice>> getProductPrice(List<String> productIds) {
        String productIdsStr = String.join(",", productIds);
        HttpHeaders productIdsRequestHeaders = mVideoHeadersService.getIdsHeaders();
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
        HttpHeaders productDetailsRequestHeaders = mVideoHeadersService.getDetailsHeaders(searchName);
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
