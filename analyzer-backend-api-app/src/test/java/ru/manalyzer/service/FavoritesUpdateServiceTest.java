package ru.manalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Flux;
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

    private final FavoritesService favoritesService = mock(FavoritesService.class);

    @BeforeEach
    public void init() {
        favoritesUpdateService = new FavoritesUpdateServiceImpl(favoritesService);
    }

    @Test
    public void updateFavoritesTest() {

    }
}
