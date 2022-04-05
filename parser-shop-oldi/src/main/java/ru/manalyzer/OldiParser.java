package ru.manalyzer;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import ru.manalyzer.dto.ConverterDto;
import ru.manalyzer.dto.OldiResponseDto;
import ru.manalyzer.property.OldiConnectionProperties;
import ru.manalyzer.dto.ProductDto;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
@Log4j2
public class OldiParser implements Parser {

    private final OldiConnectionProperties connectionProperties;

    private final ConverterDto converterOldiDtoToDto;

    @Autowired
    public OldiParser(OldiConnectionProperties connectionProperties, ConverterDto converterDto) {
        this.connectionProperties = connectionProperties;
        this.converterOldiDtoToDto = converterDto;
    }

    @Override
    public Flux<ProductDto> parse(String searchName) {
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
                .bodyToMono(OldiResponseDto.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(500))
                                .filter(throwable -> throwable instanceof TimeoutException)
                )
                .doOnError(this::exceptionHandle)
                .onErrorReturn(new OldiResponseDto())
                .flatMapIterable(OldiResponseDto::getProducts)
                .filter(oldiProductDto -> oldiProductDto.getName().toLowerCase().contains(searchName.toLowerCase()))
                .map(converterOldiDtoToDto::convertToDto);
    }

    private void exceptionHandle(Throwable throwable) {
        log.error("Oldi parser error");
    }
}
