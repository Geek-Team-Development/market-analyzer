package ru.manalyzer.service;

import org.springframework.stereotype.Service;
import ru.manalyzer.persist.Product;
import ru.manalyzer.repository.ProductRepository;

import java.util.Optional;

@Service
public class DatabaseProductServiceImpl implements DatabaseProductService {

    private final ProductRepository productRepository;

    public DatabaseProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findProductByShopIdAndShopName(String productId, String shopName) {
        return productRepository.findByProductShopIdAndShopName(productId, shopName);
    }
}
