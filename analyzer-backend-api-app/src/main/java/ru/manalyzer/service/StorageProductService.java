package ru.manalyzer.service;

import ru.manalyzer.persist.Product;

import java.util.Optional;

public interface StorageProductService {

    Optional<Product> findProductByShopIdAndShopName(String shopId, String shopName);

    Product saveProduct(Product product);
}
