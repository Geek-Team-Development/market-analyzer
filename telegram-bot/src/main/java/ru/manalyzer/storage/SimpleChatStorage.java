package ru.manalyzer.storage;

import org.springframework.stereotype.Component;
import ru.manalyzer.entity.CardSlider;
import ru.manalyzer.entity.ProductCardSlider;
import ru.manalyzer.entity.ProductSliderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SimpleChatStorage implements ChatStorage {

    private final Map<String, ProductCardSlider> productSliders;

    private final ProductSliderFactory productSliderFactory;

    public SimpleChatStorage(ProductSliderFactory productSliderFactory) {
        this.productSliderFactory = productSliderFactory;
        productSliders = new HashMap<>();
    }

    @Override
    public Optional<CardSlider> getCardSlider(String chatId) {
        return Optional.ofNullable(productSliders.get(chatId));
    }

    @Override
    public CardSlider createCardSlider(String chatId) {
        ProductCardSlider productSlider = productSliderFactory.getInstance();
        productSliders.put(chatId, productSlider);
        return productSlider;
    }
}
