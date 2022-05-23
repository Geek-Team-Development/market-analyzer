package ru.manalyzer.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.manalyzer.config.UpdateServiceConfiguration;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.ReactiveFavoriteRepository;
import ru.manalyzer.repository.UserRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class FavoritesUpdateServiceImpl implements FavoritesUpdateService {

    private final ReactiveFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final FavoritesService favoritesService;
    private final UpdateServiceConfiguration updateServiceConfiguration;

    public FavoritesUpdateServiceImpl(ReactiveFavoriteRepository favoriteRepository,
                                      UserRepository userRepository,
                                      FavoritesService favoritesService,
                                      UpdateServiceConfiguration updateServiceConfiguration) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.favoritesService = favoritesService;
        this.updateServiceConfiguration = updateServiceConfiguration;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void updateFavorites() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::update,
                updateServiceConfiguration.getInitialDelay(),
                updateServiceConfiguration.getDelayBetweenUpdate(),
                TimeUnit.valueOf(updateServiceConfiguration.getTimeUnit()));
    }

    private void update() {
        favoriteRepository.findAll()
                .subscribe(favorite -> {
                    User user = userRepository.findById(favorite.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    favoritesService.getFavoritesCartOfUser(user.getEmail()).subscribe();
                });
    }
}
