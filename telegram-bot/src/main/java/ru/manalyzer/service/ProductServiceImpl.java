package ru.manalyzer.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.property.RestConnectionProperties;

@Service
public class ProductServiceImpl implements ProductService {

    private final RestConnectionProperties restConnectionProperty;

    private final WebClient webClient;

    public ProductServiceImpl(RestConnectionProperties restConnectionProperty) {
        this.restConnectionProperty = restConnectionProperty;
        this.webClient = WebClient.create(restConnectionProperty.getUri());
    }

    @Override
    public Flux<ProductDto> findProducts(String searchName) {
        ParameterizedTypeReference<ServerSentEvent<ProductDto>> typeRef =
                new ParameterizedTypeReference<>() {};

        return webClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path(restConnectionProperty.getProductSearchPath());
                    uriBuilder.queryParam(restConnectionProperty.getSearchParamName(), searchName);
                    return uriBuilder.build();
                })
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .retrieve()
                .bodyToFlux(typeRef)
                .mapNotNull(ServerSentEvent::data);
    }
}
