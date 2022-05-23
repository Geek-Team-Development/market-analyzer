package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.repository.FavoriteRepository;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatabaseFavoritesServiceImpl implements DatabaseFavoritesService {

    private final FavoriteRepository favoriteRepository;

    private final DatabaseProductService databaseProductService;

    private final TelegramUserService telegramUserService;

    private final Mapper<Product, ProductDto> productMapper;

    @Autowired
    public DatabaseFavoritesServiceImpl(FavoriteRepository favoriteRepository,
                                        DatabaseProductService databaseProductService,
                                        TelegramUserService telegramUserService,
                                        Mapper<Product, ProductDto> productMapper) {
        this.favoriteRepository = favoriteRepository;
        this.databaseProductService = databaseProductService;
        this.telegramUserService = telegramUserService;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDto> getUserFavoriteProduct(String userId) {
        Optional<Favorite> favorite = favoriteRepository.findByUserId(userId);
        return favorite.map(value -> value.getProducts().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public boolean userFavoritesContainsProduct(String userId, ProductDto productDto) {
        Optional<Favorite> favorite = favoriteRepository.findByUserId(userId);

        if (favorite.isEmpty()) {
            return false;
        }

        return favorite.get().getProducts().stream()
                        .map(productMapper::toDto)
                        .anyMatch(productDto::equals);
    }

    @Override
    public void saveProductToFavoritesCart(ProductCardSlider productCardSlider) {
        String userId = telegramUserService.getUserIdByUserChat(productCardSlider.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElse(getNewFavorite(userId));

        Product product = saveOrUpdateProduct(productCardSlider.navigator().getCurrentProduct());
        favorite.getProducts().add(product);
        favoriteRepository.save(favorite);
    }

    @Override
    public void deleteProductFromFavoritesCart(ProductCardSlider productCardSlider) {
        String userId = telegramUserService.getUserIdByUserChat(productCardSlider.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Favorite favorite = favoriteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Favorites not found"));
        ProductDto currentProduct = productCardSlider.navigator().getCurrentProduct();
        Product deletingProduct = databaseProductService
                .findProductByShopIdAndShopName(currentProduct.getId(), currentProduct.getShopName())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        favorite.getProducts().remove(deletingProduct);
        favoriteRepository.save(favorite);
    }

    private Product saveOrUpdateProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);

        databaseProductService.findProductByShopIdAndShopName(productDto.getId(), productDto.getShopName())
                .ifPresent(value -> product.setId(value.getId()));

        return databaseProductService.saveProduct(product);
    }

    private Favorite getNewFavorite(String userId) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        return favorite;
    }
}
