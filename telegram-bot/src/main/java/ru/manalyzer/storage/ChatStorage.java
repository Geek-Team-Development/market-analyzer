package ru.manalyzer.storage;


import ru.manalyzer.entity.CardSlider;

import java.util.Optional;

public interface ChatStorage {

    Optional<CardSlider> getCardSlider(String chatId);

    CardSlider createCardSlider(String chatId);
}
