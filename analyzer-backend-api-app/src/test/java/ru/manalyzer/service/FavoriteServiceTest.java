package ru.manalyzer.service;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.manalyzer.Parser;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.PriceMapper;
import ru.manalyzer.mapper.ProductToProductPriceMapper;
import ru.manalyzer.mapper.ProductMapper;
import ru.manalyzer.parser.mvideo.MVideoParser;
import ru.manalyzer.parsers.citilink.CitilinkParser;
import ru.manalyzer.parsers.oldi.OldiParser;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;
import ru.manalyzer.repository.FavoriteRepository;
import ru.manalyzer.repository.ProductPriceRepository;
import ru.manalyzer.repository.ReactiveFavoriteRepository;
import ru.manalyzer.service.dto.ProductUpdateDto;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FavoriteServiceTest {

    private FavoritesService favoritesService;

    private final Parser oldiParser = mock(OldiParser.class);

    private final Parser mvideoParser = mock(MVideoParser.class);

    private final Parser citilinkParser = mock(CitilinkParser.class);

    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);

    private final ReactiveFavoriteRepository reactiveFavoriteRepository = mock(ReactiveFavoriteRepository.class);

    private final ProductPriceRepository productPriceRepository = mock(ProductPriceRepository.class);

    private final AuthenticationService authenticationService = mock(AuthenticationService.class);

    private final StorageProductService storageProductService = mock(StorageProductService.class);

    private ProductDto macbookPro;

    private ProductDto macbookAir;

    private ProductDto macMini;

    private final String oldiShopName = "Oldi";

    private final String mvideoShopName = "M.Video";

    private final String citilinkShopName = "Citilink";

    private final Mapper<Product, ProductDto> productMapper =
            new ProductMapper<>(new ModelMapper(), Product.class, ProductDto.class);

    private final PriceMapper<ProductPrice, Product> productDtoToProductPriceMapper =
            new ProductToProductPriceMapper<>(new ModelMapper(), ProductPrice.class, Product.class);

    @BeforeEach
    public void init() {
        Map<String, Parser> parserMap = new HashMap<>();
        parserMap.put(oldiShopName, oldiParser);
        parserMap.put(mvideoShopName, mvideoParser);
        parserMap.put(citilinkShopName, citilinkParser);

        favoritesService = new FavoritesServiceImpl(
                favoriteRepository,
                reactiveFavoriteRepository,
                productPriceRepository,
                authenticationService,
                storageProductService,
                productMapper,
                productDtoToProductPriceMapper,
                parserMap
        );

        macbookPro = new ProductDto("1",
                "Macbook Pro",
                "190000",
                "https://www.mvideo.ru/catalog/element/1",
                "https://img.mvideo.ru/",
                mvideoShopName
        );

        macbookAir = new ProductDto("2",
                "Macbook Air",
                "150000",
                "https://www.oldi.ru/catalog/element/2",
                "https://img.oldi.ru/",
                oldiShopName
        );

        macMini = new ProductDto("3",
                "Mac mini",
                "160000",
                "https://www.citilink.ru/catalog/element/2",
                "https://img.citilink.ru/",
                citilinkShopName
        );
    }

    @Test
    public void getFavoritesCartOfUserTest() {
        when(reactiveFavoriteRepository.findByUserId(any()))
                .thenAnswer(invocationOnMock -> {
                    Mono<String> userIdMono = invocationOnMock.getArgument(0);
                    String userId = userIdMono.block();
                    assertNotNull(userId);
                    if(userId.equals("test")) {
                        return Flux.just(new Favorite(
                                "test",
                                Set.of(productMapper.toEntity(macbookPro), productMapper.toEntity(macbookAir),
                                        productMapper.toEntity(macMini))
                        ));
                    }
                    return null;
                });

        when(oldiParser.parseOneProduct(Mockito.argThat(dto -> dto.getShopName().equals(oldiShopName))))
                .thenReturn(Mono.just(macbookAir));
        when(mvideoParser.parseOneProduct(Mockito.argThat(dto -> dto.getShopName().equals(mvideoShopName))))
                .thenReturn(Mono.just(macbookPro));
        when(citilinkParser.parseOneProduct(Mockito.argThat(dto -> dto.getShopName().equals(citilinkShopName))))
                .thenReturn(Mono.just(macMini));

        UserDto userDto = new UserDto();
        userDto.setId("test");
        userDto.setEmail("test@mail.ru");
        when(authenticationService.findUserByEmail("test@mail.ru"))
                .thenReturn(userDto);

        Flux<ProductDto> productDtoFlux = favoritesService.getFavoritesCartOfUser("test@mail.ru");

        StepVerifier.create(productDtoFlux)
                .expectSubscription()
                .assertNext(dto -> MatcherAssert.assertThat(dto.getName(),
                        Matchers.in(List.of("Macbook Pro", "Macbook Air", "Mac mini"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getName(),
                        Matchers.in(List.of("Macbook Pro", "Macbook Air", "Mac mini"))))
                .assertNext(dto -> MatcherAssert.assertThat(dto.getName(),
                        Matchers.in(List.of("Macbook Pro", "Macbook Air", "Mac mini"))))
                .expectNoEvent(Duration.ofMillis(500))
                .thenCancel()
                .verify();

    }

    @Test
    public void updateTest() {
        ((ProductMapper)productMapper).initMapper();
        ((ProductToProductPriceMapper)productDtoToProductPriceMapper).initMapper();
        ProductDto newMacbookProDto = new ProductDto();
        newMacbookProDto.setId(macbookPro.getId());
        newMacbookProDto.setShopName(macbookPro.getShopName());
        BigDecimal newPrice = new BigDecimal(macbookPro.getPrice()).add(new BigDecimal("2000"));
        newMacbookProDto.setPrice(newPrice.toString());
        newMacbookProDto.setProductLink(macbookPro.getProductLink());
        newMacbookProDto.setImageLink(macbookPro.getImageLink());
        newMacbookProDto.setName(macbookPro.getName());
        Product newMacbookPro = productMapper.toEntity(newMacbookProDto);
        Product oldMacbookPro = productMapper.toEntity(macbookPro);
        Product oldMacBookProAir = productMapper.toEntity(macbookAir);
        Product oldMacMini = productMapper.toEntity(macMini);

        ProductPrice expectedProductPrice = productDtoToProductPriceMapper.toProductPrice(newMacbookPro);

        Set<Product> set = new HashSet<>();
        set.add(oldMacbookPro);
        set.add(oldMacBookProAir);
        set.add(oldMacMini);

        when(reactiveFavoriteRepository.findAll())
                .thenReturn(Flux.just(new Favorite("test", set)));
        when(oldiParser.parseOneProduct(Mockito.argThat(dto -> dto.getShopName().equals(oldiShopName))))
                .thenReturn(Mono.just(macbookAir));
        when(mvideoParser.parseOneProduct(Mockito.argThat(dto -> dto.getShopName().equals(mvideoShopName))))
                .thenReturn(Mono.just(newMacbookProDto));
        when(citilinkParser.parseOneProduct(Mockito.argThat(dto -> dto.getShopName().equals(citilinkShopName))))
                .thenReturn(Mono.just(macMini));
        when(storageProductService.saveProduct(newMacbookPro))
                .thenReturn(newMacbookPro);

        ArgumentCaptor<ProductPrice> captor = ArgumentCaptor.forClass(ProductPrice.class);

        Flux<ProductUpdateDto> productUpdateDtoFlux = favoritesService.update();

        StepVerifier.create(productUpdateDtoFlux)
                .expectSubscription()
                .assertNext(productUpdateDto -> {
                    assertEquals(macbookPro, productUpdateDto.getOldProductDto());
                    assertEquals(newMacbookProDto, productUpdateDto.getNewProductDto());
                })
                .thenCancel()
                .verify();;

        verify(productPriceRepository, times(1)).save(any());
        verify(productPriceRepository).save(captor.capture());
        ProductPrice resultProductPrice = captor.getValue();
        assertEquals(expectedProductPrice.getProductId(), resultProductPrice.getProductId());
        assertEquals(expectedProductPrice.getPrice(), resultProductPrice.getPrice());
    }
}
