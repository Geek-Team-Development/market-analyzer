package ru.manalyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class FavoritesUpdateServiceImpl implements FavoritesUpdateService {

    private final FavoritesService favoritesService;

    private static final Logger logger = LoggerFactory.getLogger(FavoritesUpdateServiceImpl.class);

    public FavoritesUpdateServiceImpl(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Override
    @Scheduled(fixedRateString = "${update.service.delayBetweenUpdate}",
            initialDelayString = "${update.service.initialDelay}",
            timeUnit = TimeUnit.HOURS)
    public void updateFavorites() {
        logger.info("Запустилось обновление продуктов из избранного");
        favoritesService.update();
        logger.info("Закончилось обновление продуктов из избранного");
    }

}
