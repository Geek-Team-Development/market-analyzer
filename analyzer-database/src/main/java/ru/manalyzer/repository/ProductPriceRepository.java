package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.manalyzer.persist.ProductPrice;

import java.util.List;

public interface ProductPriceRepository extends MongoRepository<ProductPrice, String> {

    List<ProductPrice> findByProductId(String productId);
}
