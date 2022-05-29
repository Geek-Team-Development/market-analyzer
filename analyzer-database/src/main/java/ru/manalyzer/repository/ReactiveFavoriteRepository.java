package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;

public interface ReactiveFavoriteRepository extends ReactiveMongoRepository<Favorite, String> {

    Flux<Favorite> findByUserId(Mono<String> userId);

    Flux<Favorite> findByProductsContains(Product product);
}
