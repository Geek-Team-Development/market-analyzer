package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.PriceMapper;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.FavoriteRepository;
import ru.manalyzer.repository.ProductPriceRepository;
import ru.manalyzer.repository.ReactiveFavoriteRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoriteRepository favoriteRepository;

    private final ReactiveFavoriteRepository reactiveFavoriteRepository;

    private final ProductPriceRepository productPriceRepository;

    private final AuthenticationService authenticationService;

    private final UserService userService;

    private final TelegramService telegramService;

    private final StorageProductService storageProductService;

    private final Mapper<Product, ProductDto> productMapper;

    private final PriceMapper<ProductPrice, Product> productDtoToProductPriceMapper;

    private final Map<String, Parser> activeParserMap;

    @Autowired
    public FavoritesServiceImpl(FavoriteRepository favoriteRepository,
                                ReactiveFavoriteRepository reactiveFavoriteRepository,
                                ProductPriceRepository productPriceRepository,
                                AuthenticationService authenticationService,
                                UserService userService, TelegramService telegramService, StorageProductService storageProductService,
                                Mapper<Product, ProductDto> productMapper,
                                PriceMapper<ProductPrice, Product> productDtoToProductPriceMapper,
                                @Qualifier("activeParserMap") Map<String, Parser> activeParserMap) {
        this.favoriteRepository = favoriteRepository;
        this.reactiveFavoriteRepository = reactiveFavoriteRepository;
        this.productPriceRepository = productPriceRepository;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.telegramService = telegramService;
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
                            activeParserMap.get(productDto.getShopName())
                                    .parseOneProduct(productDto)
                                    .subscribe(dto -> {
                                        if (!productDto.equals(dto)) {
                                            fluxSink.next(dto);

                                            // if change price then notify users
                                            if (!productDto.getPrice().equals(dto.getPrice())) {
                                                System.out.println("New product price " + dto.getPrice());
                                                System.out.println("Old product price " + productDto.getPrice());
                                                dto.setOldPrice(productDto.getPrice());
                                                notifyUser(dto);
                                            }
                                            saveProductPrice(saveOrUpdateProduct(dto));
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

    private void notifyUser(ProductDto dto) {
        Product product = storageProductService
                .findProductByShopIdAndShopName(dto.getId(), dto.getShopName())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        reactiveFavoriteRepository.findByProductsContains(product)
                .subscribe(favorites -> {
                    userService.getTelegramChatIdByUserId(favorites.getUserId())
                            .ifPresent(chatId -> telegramService.notifyUsersAboutChangePrice(chatId, dto));
                });
//        reactiveFavoriteRepository.findByProductsContains(product)
//                .map(favorites -> {
//                    System.out.println("favorites id=" + favorites.getId());
//                    return userService.getUserById(favorites.getUserId());
//                })
//                .filter(Optional::isPresent)
//                .map(opt -> opt.get().getTelegramChatId())
//                .filter(chatId -> !chatId.isBlank())
//                .subscribe(chatId -> telegramService.notifyUsersAboutChangePrice(chatId, dto));
    }
}
