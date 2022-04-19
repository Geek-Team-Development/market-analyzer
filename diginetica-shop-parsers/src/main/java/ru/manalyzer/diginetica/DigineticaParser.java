package ru.manalyzer.diginetica;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import ru.manalyzer.Parser;
import ru.manalyzer.diginetica.dto.ConverterDto;
import ru.manalyzer.diginetica.dto.DigineticaResponseDto;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;
import ru.manalyzer.dto.ProductDto;

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
    public Flux<ProductDto> parse(String searchName) {
        // Set search query parameter
        connectionProperties.getQueryParams().put(connectionProperties.getSearchParamName(), searchName);

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
                .onErrorReturn(new DigineticaResponseDto())
                .flatMapIterable(DigineticaResponseDto::getProducts)
                .filter(oldiProductDto -> oldiProductDto.getName().toLowerCase().contains(searchName.toLowerCase()))
                .map(converterDigineticaDtoToDto::convertToDto);
    }

    private void exceptionHandle(Throwable throwable) {
        log.error(connectionProperties.getShopName() + " parser error");
    }
}