package ru.manalyzer.service;

import ru.manalyzer.persist.Product;

import java.util.Optional;

public interface DatabaseProductService {

    Product saveProduct(Product product);

    Optional<Product> findProductByShopIdAndShopName(String productId, String shopName);
}
