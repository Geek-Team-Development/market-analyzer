package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.DocumentPointer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.PriceMapper;
import ru.manalyzer.mapper.ProductMapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;
import ru.manalyzer.repository.FavoriteRepository;
import ru.manalyzer.repository.ProductPriceRepository;
import ru.manalyzer.repository.ReactiveFavoriteRepository;
import ru.manalyzer.service.dto.ProductUpdateDto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        return Flux.create(fluxSink -> reactiveFavoriteRepository.findByUserId(Mono.just(getUserId(userLogin)))
                .subscribe(favorite -> favorite.getProducts().parallelStream().forEach(product -> {
                            ProductDto productDto = productMapper.toDto(product);
                            fluxSink.next(productDto);
                            updateOneProduct(productDto)
                                    .subscribe(dto -> {
                                        if (!productDto.equals(dto)) {
                                            fluxSink.next(dto);
                                        }
                                    });
                        }
                )));

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
        return Flux.create(fluxSink -> {
            reactiveFavoriteRepository.findAll()
                    .flatMap(favorite -> Flux.fromIterable(favorite.getProducts()))
                    .map(productMapper::toDto)
                    .distinct()
                    .subscribe(productDto ->
                            updateOneProduct(productDto).subscribe(dto -> {
                                if(!productDto.equals(dto)) {
                                    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
                                    productUpdateDto.setOldProductDto(productDto);
                                    productUpdateDto.setNewProductDto(dto);
                                    fluxSink.next(productUpdateDto);
                                }
                            }));
        });
    }

    private Mono<ProductDto> updateOneProduct(ProductDto productDto) {
        return Mono.create(monoSink -> {
            activeParserMap.get(productDto.getShopName())
                    .parseOneProduct(productDto)
                    .subscribe(dto -> {
                        if (!productDto.equals(dto)) {
                            saveProductPrice(saveOrUpdateProduct(dto));
                        }
                        monoSink.success(dto);
                    });
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
