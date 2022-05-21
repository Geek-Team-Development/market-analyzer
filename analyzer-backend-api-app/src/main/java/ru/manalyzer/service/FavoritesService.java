package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;

public interface FavoritesService {

    Flux<ProductDto> getFavoritesCartOfUser(String userLogin);

    void saveProductToFavoritesCart(ProductDto productDto, String userLogin);

    void deleteProductFromFavoritesCart(String productId, String shopName, String userLogin);

    void clearFavoritesCart(String userLogin);

    void update();
}
