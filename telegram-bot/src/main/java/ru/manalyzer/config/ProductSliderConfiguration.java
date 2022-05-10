package ru.manalyzer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.manalyzer.entity.CardButton;
import ru.manalyzer.entity.ProductCardSlider;
import ru.manalyzer.entity.ProductSliderFactory;
import ru.manalyzer.format.CardFormat;

@Configuration
public class ProductSliderConfiguration {

    private final CardFormat cardFormat;

    private final CardButton cardButton;

    @Autowired
    public ProductSliderConfiguration(CardFormat cardFormat, CardButton cardButton) {
        this.cardFormat = cardFormat;
        this.cardButton = cardButton;
    }

    @Bean
    @Scope("prototype")
    ProductCardSlider getProductSlider() {
        return new ProductCardSlider(cardFormat, cardButton);
    }

    @Bean
    public ProductSliderFactory getProductSliderFactory() {
        return new ProductSliderFactory() {
            @Override
            public ProductCardSlider getInstance() {
                return getProductSlider();
            }
        };
    }

}
