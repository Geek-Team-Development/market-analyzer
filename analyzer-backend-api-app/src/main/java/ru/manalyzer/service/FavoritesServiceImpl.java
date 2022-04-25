package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.repository.FavoriteRepository;

import java.util.Optional;
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
        String userId = getUserId(userLogin);
        Favorite favoriteCart = favoriteRepository.findByUserId(userId)
                .orElse(getNewFavorite(userId));

        favoriteCart.getProducts().add(getProduct(productDto));

        favoriteRepository.save(favoriteCart);
    }

    @Override
    public void deleteProductFromFavoritesCart(String productId, String shopName, String userLogin) {
        Optional<Product> productOpt = storageProductService.findProductByShopIdAndShopName(productId, shopName);
        if (productOpt.isPresent()) {
            Optional<Favorite> favoriteOpt = favoriteRepository.findByUserId(getUserId(userLogin));

            if (favoriteOpt.isPresent()) {
                Favorite favorite = favoriteOpt.get();
                favorite.getProducts().remove(productOpt.get());
                favoriteRepository.save(favorite);
            }
        }
    }

    @Override
    public void clearFavoritesCart(String userLogin) {
        favoriteRepository.findByUserId(getUserId(userLogin))
                .ifPresent(favorite -> {
                    favorite.getProducts().clear();
                    favoriteRepository.save(favorite);
                });
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
        Product product = productMapper.toEntity(productDto);

        storageProductService.findProductByShopIdAndShopName(productDto.getId(), productDto.getShopName())
                .ifPresent(value -> product.setId(value.getId()));

        return storageProductService.saveProduct(product);
    }

}
