package ru.manalyzer.telegram.slider;

import org.springframework.stereotype.Component;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.property.CardSliderCaptionProperties;
import ru.manalyzer.storage.entity.ProductCardSlider;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Component
public class CardSliderCaptionFormatter implements CardSliderFormatter {

    private final CardSliderCaptionProperties cardSliderCaptionProperties;

    public CardSliderCaptionFormatter(CardSliderCaptionProperties cardSliderProperties) {
        this.cardSliderCaptionProperties = cardSliderProperties;
    }

    private String formatTitle(String title) {
        return String.format(cardSliderCaptionProperties.getTitleFormat(), title);
    }

    private String formatPrice(String price) {
        BigDecimal bigDecimalPrice = new BigDecimal(price);
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return String.format(cardSliderCaptionProperties.getPriceFormat(), currency.format(bigDecimalPrice));
    }

    private String formatShopLink(String productLink, String shopName) {
        return String.format(cardSliderCaptionProperties.getShopLinkFormat(), productLink, shopName);
    }

    @Override
    public String format(ProductCardSlider productCardSlider) {
        ProductDto productDto = productCardSlider.navigator().getCurrentProduct();
        return formatTitle(productDto.getName()) +
                formatPrice(productDto.getPrice()) +
                formatShopLink(productDto.getProductLink(), productDto.getShopName());
    }
}
