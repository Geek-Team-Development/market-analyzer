package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.PriceMapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;
import ru.manalyzer.repository.FavoriteRepository;
import ru.manalyzer.repository.ProductPriceRepository;
import ru.manalyzer.repository.ReactiveFavoriteRepository;
import ru.manalyzer.service.dto.ProductUpdateDto;

import java.util.Map;
import java.util.Optional;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoriteRepository favoriteRepository;

    private final ReactiveFavoriteRepository reactiveFavoriteRepository;

    private final ProductPriceRepository productPriceRepository;

    private final AuthenticationService authenticationService;

    private final StorageProductService storageProductService;

    private final Mapper<Product, ProductDto> productMapper;

    private final PriceMapper<ProductPrice, Product> productDtoToProductPriceMapper;

    private final Map<String, Parser> activeParserMap;

    @Autowired
    public FavoritesServiceImpl(FavoriteRepository favoriteRepository,
                                ReactiveFavoriteRepository reactiveFavoriteRepository, ProductPriceRepository productPriceRepository,
                                AuthenticationService authenticationService,
                                StorageProductService storageProductService,
                                Mapper<Product, ProductDto> productMapper,
                                PriceMapper<ProductPrice, Product> productDtoToProductPriceMapper,
                                @Qualifier("activeParserMap") Map<String, Parser> activeParserMap) {
        this.favoriteRepository = favoriteRepository;
        this.reactiveFavoriteRepository = reactiveFavoriteRepository;
        this.productPriceRepository = productPriceRepository;
        this.authenticationService = authenticationService;
        this.storageProductService = storageProductService;
        this.productMapper = productMapper;
        this.productDtoToProductPriceMapper = productDtoToProductPriceMapper;
        this.activeParserMap = activeParserMap;
    }

    @Override
    public Flux<ProductDto> getFavoritesCartOfUser(String userLogin) {
        String userId = getUserId(userLogin);
        return reactiveFavoriteRepository.findByUserId(Mono.just(getUserId(userLogin)))
                .filter(favorite -> favorite.getUserId().equals(userId))
                .flatMap(favorite -> Flux.fromIterable(favorite.getProducts()))
                .map(productMapper::toDto)
                .flatMap(this::updateOneProduct);
    }

    @Override
    public void saveProductToFavoritesCart(ProductDto productDto, String userLogin) {
        String userId = getUserId(userLogin);
        Favorite favoriteCart = favoriteRepository.findByUserId(userId)
                .orElse(getNewFavorite(userId));

        Product product = saveOrUpdateProduct(productDto);
        favoriteCart.getProducts().add(product);

        favoriteRepository.save(favoriteCart);
        productPriceRepository.save(productDtoToProductPriceMapper.toProductPrice(product));
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

    private Product saveOrUpdateProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);

        storageProductService.findProductByShopIdAndShopName(productDto.getId(), productDto.getShopName())
                .ifPresent(value -> product.setId(value.getId()));

        return storageProductService.saveProduct(product);
    }

    private void saveProductPrice(Product product) {
        ProductPrice productPrice = productDtoToProductPriceMapper.toProductPrice(product);

        productPriceRepository.save(productPrice);
    }

    @Override
    public Flux<ProductUpdateDto> update() {

        return reactiveFavoriteRepository.findAll()
                .flatMap(favorite -> Flux.fromIterable(favorite.getProducts()))
                .map(productMapper::toDto)
                .distinct()
                .flatMap(productDto ->
                        updateOneProduct(productDto)
                                .filter(updatedDto -> !productDto.equals(updatedDto))
                                .map(updatedDto -> {
                                    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
                                    productUpdateDto.setOldProductDto(productDto);
                                    productUpdateDto.setNewProductDto(updatedDto);
                                    return productUpdateDto;
                        })
                );
    }

    private Mono<ProductDto> updateOneProduct(ProductDto productDto) {
        return activeParserMap.get(productDto.getShopName())
                .parseOneProduct(productDto)
                .mapNotNull(updatedDto -> {
                    if (!productDto.equals(updatedDto)) {
                        saveProductPrice(saveOrUpdateProduct(updatedDto));
                        return updatedDto;
                    }
                    return productDto;
                });
    }

    @Override
    public Flux<String> getUsersWithProduct(ProductDto productDto) {
        return reactiveFavoriteRepository.findAll()
                .filter(favorite -> favorite.getProducts()
                        .stream()
                        .anyMatch(product -> product.getProductShopId().equals(productDto.getId())))
                .map(Favorite::getUserId);
    }
}
