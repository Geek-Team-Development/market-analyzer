package ru.manalyzer.service;

import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.service.dto.ProductUpdateDto;

public interface FavoritesService {

    Flux<ProductDto> getFavoritesCartOfUser(String userLogin);

    void saveProductToFavoritesCart(ProductDto productDto, String userLogin);

    void deleteProductFromFavoritesCart(String productId, String shopName, String userLogin);

    void clearFavoritesCart(String userLogin);

    void update();

    Flux<String> getUsersWithProduct(ProductDto productDto);

    void addProductUpdateListener(Object object, ProductUpdateListener listener);

    void removeProductUpdateListener(Object object);
}
