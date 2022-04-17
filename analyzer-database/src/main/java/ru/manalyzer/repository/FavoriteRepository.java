package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.manalyzer.persist.Favorite;

import java.util.Optional;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    Optional<Favorite> findByUserId(String userId);
}
