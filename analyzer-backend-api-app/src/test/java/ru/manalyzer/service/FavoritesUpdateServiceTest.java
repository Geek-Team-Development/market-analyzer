package ru.manalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import ru.manalyzer.config.UpdateServiceConfiguration;
import ru.manalyzer.persist.Favorite;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.ReactiveFavoriteRepository;
import ru.manalyzer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class FavoritesUpdateServiceTest {

    private FavoritesUpdateService favoritesUpdateService;

    private final ReactiveFavoriteRepository favoriteRepository = mock(ReactiveFavoriteRepository.class);

    private final UserRepository userRepository = mock(UserRepository.class);

    private final FavoritesService favoritesService = mock(FavoritesService.class);

    private final UpdateServiceConfiguration updateServiceConfiguration = new UpdateServiceConfiguration();

    private final List<User> users = new ArrayList<>();

    private final List<Favorite> favorites = new ArrayList<>();

    @BeforeEach
    public void init() {
        updateServiceConfiguration.setInitialDelay(0);
        updateServiceConfiguration.setDelayBetweenUpdate(2);
        updateServiceConfiguration.setTimeUnit(TimeUnit.SECONDS.toString());
        favoritesUpdateService = new FavoritesUpdateServiceImpl(favoriteRepository,
                userRepository, favoritesService, updateServiceConfiguration);

        for (int i = 1; i <= 2; i++) {
            User user = new User();
            user.setId(Integer.toString(i));
            user.setEmail("email" + i);
            users.add(user);
        }

        Favorite favorite = new Favorite();
        favorite.setId("1");
        favorite.setUserId(users.get(0).getId());
        favorites.add(favorite);

        favorite = new Favorite();
        favorite.setId("2");
        favorite.setUserId(users.get(1).getId());
        favorites.add(favorite);
    }

    @Test
    public void updateFavoritesTest() {
        doReturn(Flux.fromIterable(favorites))
                .when(favoriteRepository).findAll();
        doReturn(Optional.of(users.get(0)))
                .when(userRepository).findById("1");
        doReturn(Optional.of(users.get(1)))
                .when(userRepository).findById("2");
        users.forEach(user ->
                when(favoritesService.getFavoritesCartOfUser(user.getEmail()))
                        .thenReturn(Flux.empty())
                        .thenReturn(Flux.empty()));

        favoritesUpdateService.updateFavorites();
        users.forEach(user ->
                verify(favoritesService, timeout(3000).times(2))
                        .getFavoritesCartOfUser(user.getEmail()));
    }
}
