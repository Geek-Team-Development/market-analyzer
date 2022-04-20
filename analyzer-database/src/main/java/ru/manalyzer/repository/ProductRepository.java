package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.manalyzer.persist.Product;

import java.util.Optional;


public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByProductShopIdAndShopName(String productShopId, String shopName);
}
