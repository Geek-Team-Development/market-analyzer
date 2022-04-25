package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.manalyzer.persist.Product;
import ru.manalyzer.repository.ProductRepository;

import java.util.Optional;

@Service
public class StorageProductServiceImpl implements StorageProductService {

    private final ProductRepository productRepository;

    @Autowired
    public StorageProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Optional<Product> findProductByShopIdAndShopName(String shopId, String shopName) {
        return productRepository.findByProductShopIdAndShopName(shopId, shopName);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

}
