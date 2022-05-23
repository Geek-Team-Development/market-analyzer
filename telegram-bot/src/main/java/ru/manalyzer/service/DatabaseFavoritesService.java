package ru.manalyzer.service;

import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.util.List;

public interface DatabaseFavoritesService {

    List<ProductDto> getUserFavoriteProduct(String userId);

    boolean userFavoritesContainsProduct(String userId, ProductDto productDto);

    void saveProductToFavoritesCart(ProductCardSlider productCardSlider);

    void deleteProductFromFavoritesCart(ProductCardSlider productCardSlider);
}
