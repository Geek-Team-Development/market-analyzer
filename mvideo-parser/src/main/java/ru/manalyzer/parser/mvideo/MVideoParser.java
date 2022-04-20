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
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.parser.mvideo.config.MVideoProperties;
import ru.manalyzer.parser.mvideo.dto.*;
import ru.manalyzer.parser.mvideo.service.MVideoHeadersService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    public Flux<ProductDto> parse(String searchName) {
        logger.info("Products requested with name {}", searchName);
        return getProductIds(searchName).flatMapMany(productIds -> {
            if(productIds.size() == 0) {
                return Flux.empty();
            }
            Flux<MaterialPrice> prices = getProductPrice(productIds)
                    .sort(Comparator.comparing(MaterialPrice::getProductId));
            Flux<ProductDetail> details = getProductDetails(productIds, searchName)
                    .sort(Comparator.comparing(ProductDetail::getProductId));
            return Flux.zip(prices, details).map(tuple -> {
                ProductDto product = new ProductDto();
                MaterialPrice price = tuple.getT1();
                ProductDetail detail = tuple.getT2();
                String id = price.getProductId();
                product.setId(id);
                product.setShopName(properties.getShopName());
                product.setPrice(price.getPrice().getBasePrice());
                product.setName(detail.getName());
                product.setImageLink(properties.getImageLinkPrefix() + detail.getImage());
                product.setProductLink(properties.getProductLinkPrefix() + detail.getNameTranslit() + "-" + id);
                return product;
            });
        });
    }

    private Mono<List<String>> getProductIds(String searchName) {
        HttpHeaders productIdsRequestHeaders = mVideoHeadersService.getIdsHeaders();
        return webClient
                .get()
                .uri(uriBuilder -> {
                    String searchParamName = properties.getIdsRequest().getSearchParamName();
                    uriBuilder
                            .path(properties.getSearchUrl())
                            .queryParam(searchParamName, searchName);
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

    private Flux<MaterialPrice> getProductPrice(List<String> productIds) {
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
                .flatMapIterable(productPrices -> productPrices.getBody().getMaterialPrices());
    }

    private Flux<ProductDetail> getProductDetails(List<String> productIds,
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
                .flatMapIterable(productDetails -> productDetails.getBody().getProducts());
    }
}
