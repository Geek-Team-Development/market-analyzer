package ru.manalyzer.telegram.notification;

import org.springframework.stereotype.Component;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.property.PriceNotificationProperties;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Component
public class PriceNotificationFormatter implements NotificationFormatter {

    private final PriceNotificationProperties priceNotificationProperties;

    public PriceNotificationFormatter(PriceNotificationProperties priceNotificationProperties) {
        this.priceNotificationProperties = priceNotificationProperties;
    }

    private String formatTitle(String title) {
        title = title.replaceAll("[<>]", "");
        return String.format(priceNotificationProperties.getTitleFormat(), title);
    }

    private String formatOldPrice(String price) {
        BigDecimal bigDecimalPrice = new BigDecimal(price);
//        NumberFormat currency = NumberFormat.getCurrencyInstance();
        return String.format(priceNotificationProperties.getOldPriceFormat(), bigDecimalPrice);
    }

    private String formatPrice(String price, String oldPrice) {
        BigDecimal bigDecimalPrice = new BigDecimal(price);
        BigDecimal bigDecimalOldPrice = new BigDecimal(oldPrice);
//        NumberFormat currency = NumberFormat.getCurrencyInstance();
        String priceFormatted;
        if (bigDecimalPrice.compareTo(bigDecimalOldPrice) < 0) {
            priceFormatted =
                    String.format(priceNotificationProperties.getPriceDownFormat(), bigDecimalPrice);
        } else {
            priceFormatted =
                    String.format(priceNotificationProperties.getPriceUpFormat(), bigDecimalPrice);
        }
        return priceFormatted;
    }

    private String formatShopLink(String productLink, String shopName) {
        return String.format(priceNotificationProperties.getShopLinkFormat(), productLink, shopName);
    }

    @Override
    public String format(ProductDto productDto) {
        return formatTitle(productDto.getName()) +
                formatOldPrice(productDto.getOldPrice()) +
                formatPrice(productDto.getPrice(), productDto.getOldPrice()) +
                formatShopLink(productDto.getProductLink(), productDto.getShopName());
    }
}
