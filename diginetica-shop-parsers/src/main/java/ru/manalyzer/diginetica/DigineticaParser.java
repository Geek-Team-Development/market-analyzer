package ru.manalyzer.diginetica;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.manalyzer.Parser;
import ru.manalyzer.diginetica.dto.ConverterDto;
import ru.manalyzer.diginetica.dto.DigineticaProductDto;
import ru.manalyzer.diginetica.dto.DigineticaResponseDto;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.Sort;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Responsible for loading and converting product information from Diginetica store
 */
@Log4j2
public class DigineticaParser implements Parser {

    private final DigineticaConnectionProperties connectionProperties;

    private final ConverterDto converterDigineticaDtoToDto;

    public DigineticaParser(DigineticaConnectionProperties connectionProperties, ConverterDto converterDto) {
        this.connectionProperties = connectionProperties;
        this.converterDigineticaDtoToDto = converterDto;
    }

    /**
     * Loads a list of products and converts to ProductDto format
     *
     * @param searchName - parameters for connecting to the online store
     * @return Flux<ProductDto> or empty Flux<ProductDto> if an error occurred
     */
    @Override
    public Flux<ProductDto> parse(String searchName, Sort sort, int pageNumber) {
        return invokeRequest(searchName, sort, pageNumber)
                .flatMapIterable(DigineticaResponseDto::getProducts)
//                .filter(oldiProductDto -> oldiProductDto.getName().toLowerCase().contains(searchName.toLowerCase()))
                .filter(oldiProductDto -> matchProduct(searchName, oldiProductDto))
                .map(converterDigineticaDtoToDto::convertToDto);
    }

    private Mono<DigineticaResponseDto> invokeRequest(String searchString, Sort sort, int pageNumber) {
        // Set search query parameter
        connectionProperties.getQueryParams().put(connectionProperties.getSearchParamName(), searchString);
        connectionProperties.getQueryParams().put(connectionProperties.getOffsetParamName(), Integer.toString(pageNumber));
        connectionProperties.getQueryParams().put(connectionProperties.getSortParamName(), sort.toString().toUpperCase());

        WebClient client = WebClient.create(connectionProperties.getSearchUri());
        return client
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path(connectionProperties.getSearchPath());
                    connectionProperties.getQueryParams().forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DigineticaResponseDto.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(500))
                                .filter(throwable -> throwable instanceof TimeoutException)
                )
                .doOnError(this::exceptionHandle)
                .onErrorReturn(new DigineticaResponseDto());
    }

    /**
     * Loads product by productId and converts to ProductDto format
     * @param productDto - product for reload
     * @return Mono<ProductDto> or empty Mono<ProductDto> if an error occurred
     */
    @Override
    public Mono<ProductDto> parseOneProduct(ProductDto productDto) {
        return invokeRequest(productDto.getId(), Sort.price_asc, 0)
                .map(DigineticaResponseDto::getProducts)
                .filter(list -> list.size() > 0)
                .map(list -> list.get(0))
                .map(converterDigineticaDtoToDto::convertToDto);
    }

    @Override
    public Mono<ProductDto> parseProductPage(ProductDto productDto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getShopName() {
        return connectionProperties.getShopName();
    }

    private void exceptionHandle(Throwable throwable) {
        log.error(connectionProperties.getShopName() + " parser error");
    }

    private boolean matchProduct(String searchName, DigineticaProductDto digineticaProductDto) {
        String[] searchWords = searchName.split(" ");
        String productName = digineticaProductDto.getName().toLowerCase();
        for (int i = 0; i < searchWords.length; i++) {
            String searchWord = searchWords[i].toLowerCase();
            if (!productName.contains(searchWord.substring(0, searchWord.length() - 1))) {
                return false;
            }
        }
        return true;
    }
}
