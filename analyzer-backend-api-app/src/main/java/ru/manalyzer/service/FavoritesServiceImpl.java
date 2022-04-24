package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.repository.FavoriteRepository;

import java.util.stream.Collectors;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoriteRepository favoriteRepository;

    private final AuthenticationService authenticationService;

    private final StorageProductService storageProductService;

    private final Mapper<Product, ProductDto> productMapper;

    @Autowired
    public FavoritesServiceImpl(FavoriteRepository favoriteRepository,
                                AuthenticationService authenticationService,
                                StorageProductService storageProductService,
                                Mapper<Product, ProductDto> productMapper) {
        this.favoriteRepository = favoriteRepository;
        this.authenticationService = authenticationService;
        this.storageProductService = storageProductService;
        this.productMapper = productMapper;
    }

    @Override
    public Flux<ProductDto> getFavoritesCartOfUser(String userLogin) {
        return Flux.fromIterable(
                favoriteRepository.findByUserId(getUserId(userLogin)).orElse(new Favorite())
                        .getProducts().stream()
                        .map(productMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void saveProductToFavoritesCart(ProductDto productDto, String userLogin) {
        final String userId = getUserId(userLogin);
        Favorite favoriteCart = favoriteRepository.findByUserId(userId)
                .orElse(getNewFavorite(userId));

        favoriteCart.getProducts().add(getProduct(productDto));

        favoriteRepository.save(favoriteCart);
    }

    @Override
    public Flux<ProductDto> deleteProductFromFavoritesCart(String id) {
        return null;
    }

    @Override
    public Flux<ProductDto> clearFavoritesCart() {
        return null;
    }

    private String getUserId(String userLogin) {
        return authenticationService.findUserByEmail(userLogin).getId();
    }

    private Favorite getNewFavorite(String userId) {
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        return favorite;
    }

    private Product getProduct(ProductDto productDto) {
        return storageProductService.findProductByShopIdAndShopName(productDto.getId(), productDto.getShopName())
                .orElse(storageProductService.saveProduct(productMapper.toEntity(productDto)));
    }
}
