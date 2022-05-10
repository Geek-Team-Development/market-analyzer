package ru.manalyzer.format;

import org.springframework.stereotype.Component;
import ru.manalyzer.dto.ProductDto;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Component
public class ProductCardFormat implements CardFormat {

    private String formatProductName(String name) {
        return String.format("<strong>%s</strong>%n%n", name);
    }

    private String formatPrice(String price) {
        BigDecimal bigDecimalPrice = new BigDecimal(price);
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return String.format("\uD83E\uDD11  %s%n%n", currency.format(bigDecimalPrice));
    }

    private String formatShopLink(String productLink, String shopName) {
        return String.format("<a href=\"%s\">Подробнее на %s</a>", productLink, shopName);
    }

    @Override
    public String format(ProductDto productDto) {
        return formatProductName(productDto.getName()) +
                formatPrice(productDto.getPrice()) +
                formatShopLink(productDto.getProductLink(), productDto.getShopName());
    }
}
