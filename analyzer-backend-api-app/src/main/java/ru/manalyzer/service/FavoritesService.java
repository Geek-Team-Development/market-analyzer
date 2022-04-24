package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;

public interface FavoritesService {

    Flux<ProductDto> getFavoritesCartOfUser(String userLogin);

    void saveProductToFavoritesCart(ProductDto productDto, String userLogin);

    Flux<ProductDto> deleteProductFromFavoritesCart(String id);

    Flux<ProductDto> clearFavoritesCart();
}
