package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.persist.Favorite;

public interface ReactiveFavoriteRepository extends ReactiveMongoRepository<Favorite, String> {

    Flux<Favorite> findByUserId(Mono<String> userId);
}
