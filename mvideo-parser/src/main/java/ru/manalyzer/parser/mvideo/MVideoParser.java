package ru.manalyzer.parser.mvideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.parser.mvideo.dto.*;
import ru.manalyzer.parser.mvideo.service.MVideoHeadersService;

import java.util.Comparator;
import java.util.List;

/**
 * Парсер информации о продуктах с сервера MVideo
 */
@Service
public class MVideoParser implements Parser {

    @Value("${mvideo.search-url}")
    private String searchURL;
    @Value("${mvideo.price-url}")
    private String priceURL;
    @Value("${mvideo.product-details.url}")
    private String productDetailsURL;

    private final MVideoHeadersService mVideoHeadersService;
    private final WebClient webClient;

    private static final Logger logger = LoggerFactory.getLogger(MVideoParser.class);

    @Autowired
    public MVideoParser(WebClient webClient, MVideoHeadersService mVideoHeadersService) {
        this.webClient = webClient;
        this.mVideoHeadersService = mVideoHeadersService;
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
                product.setShopName("MVideo");
                product.setPrice(price.getPrice().getBasePrice());
                product.setName(detail.getName());
                product.setImageLink("https://img.mvideo.ru/" + detail.getImage());
                product.setProductLink("https://www.mvideo.ru/products/" + detail.getNameTranslit() + "-" + id);
                return product;
            });
        });
    }

    private Mono<List<String>> getProductIds(String searchName) {
        HttpHeaders productIdsRequestHeaders = mVideoHeadersService.getIdsHeaders();
        return webClient
                .get()
                .uri(uriBuilder -> UriComponentsBuilder.fromUri(uriBuilder.build())
                        .path(searchURL)
                        .queryParam("query", searchName)
                        .queryParam("offset", "0")
                        .queryParam("limit", "24")
                        .encode()
                        .build()
                        .toUri())
                .headers(httpHeaders -> httpHeaders.addAll(productIdsRequestHeaders))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MVideoResponse<ProductIds>>() {
                })
                .map(productIds -> productIds.getBody().getProducts())
                .onErrorReturn(NullPointerException.class, List.of());
    }

    private Flux<MaterialPrice> getProductPrice(List<String> productIds) {
        String productIdsStr = String.join(",", productIds);
        HttpHeaders productIdsRequestHeaders = mVideoHeadersService.getIdsHeaders();
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(priceURL)
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
                .uri(productDetailsURL)
                .headers(httpHeaders -> httpHeaders.addAll(productDetailsRequestHeaders))
                .bodyValue(postObject)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MVideoResponse<ProductDetails>>() {
                })
                .flatMapIterable(productDetails -> productDetails.getBody().getProducts());
    }
}
