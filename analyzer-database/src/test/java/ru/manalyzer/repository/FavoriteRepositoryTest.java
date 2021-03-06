package ru.manalyzer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.Role;
import ru.manalyzer.persist.User;

import java.math.BigDecimal;

@DataMongoTest
@OverrideAutoConfiguration(enabled = true)
public class FavoriteRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private static User testUser;

    private static Product testProductMacbook;

    private static Product testProductIPhone;

    @BeforeAll
    public static void init() {
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setFirstName("Test");
        testUser.setLastName("");
        testUser.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        testUser.setCity("");
        testUser.getRoles().add(Role.USER);

        testProductMacbook = new Product();
        testProductMacbook.setProductShopId("001");
        testProductMacbook.setShopName("MVideo");
        testProductMacbook.setName("Macbook");
        testProductMacbook.setCost(new BigDecimal(200000));
        testProductMacbook.setImageLink("");
        testProductMacbook.setProductLink("");

        testProductIPhone = new Product();
        testProductIPhone.setProductShopId("001");
        testProductIPhone.setShopName("Oldi");
        testProductIPhone.setName("iPhone");
        testProductIPhone.setCost(new BigDecimal(100000));
        testProductIPhone.setImageLink("");
        testProductIPhone.setProductLink("");
    }

    @Test
    public void findFavoriteByUserId() {
        userRepository.save(testUser);
        productRepository.save(testProductMacbook);
        productRepository.save(testProductIPhone);

        Favorite expectedFavorite = new Favorite();
        expectedFavorite.setUserId(testUser.getId());
        expectedFavorite.getProducts().add(testProductMacbook);
        expectedFavorite.getProducts().add(testProductIPhone);

        favoriteRepository.save(expectedFavorite);

        Favorite savedFavorite = favoriteRepository.findByUserId(testUser.getId()).get();

        Assertions.assertEquals(testUser.getId(), savedFavorite.getUserId());
        Assertions.assertEquals(2, savedFavorite.getProducts().size());
        savedFavorite.getProducts().forEach(
                product -> Assertions.assertEquals(testProductMacbook.getProductShopId(), product.getProductShopId())
        );

        favoriteRepository.delete(savedFavorite);

        productRepository.delete(testProductMacbook);
        productRepository.delete(testProductIPhone);
        userRepository.delete(testUser);
    }

    @Test
    public void favoriteNotFoundTest() {
        userRepository.save(testUser);

        Assertions.assertFalse(favoriteRepository.findByUserId(testUser.getId()).isPresent());

        userRepository.delete(testUser);
    }

    @Test
    public void emptyFavoriteTest() {
        userRepository.save(testUser);

        Favorite expectedFavorite = new Favorite();
        expectedFavorite.setUserId(testUser.getId());

        favoriteRepository.save(expectedFavorite);

        Favorite savedFavorite = favoriteRepository.findByUserId(testUser.getId()).get();

        Assertions.assertEquals(testUser.getId(), savedFavorite.getUserId());
        Assertions.assertTrue(savedFavorite.getProducts().isEmpty());

        favoriteRepository.delete(expectedFavorite);

        userRepository.delete(testUser);
    }

    @Test
    public void failDuplicateFavoriteForUserTest() {
        userRepository.save(testUser);

        Favorite expectedFavorite = new Favorite();
        expectedFavorite.setUserId(testUser.getId());

        favoriteRepository.save(expectedFavorite);

        Favorite duplicateFavorite = new Favorite();
        duplicateFavorite.setUserId(testUser.getId());

        Assertions.assertThrows(DuplicateKeyException.class, () -> favoriteRepository.save(duplicateFavorite));

        favoriteRepository.delete(expectedFavorite);

        userRepository.delete(testUser);
    }
}
