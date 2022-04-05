package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String id;

    private String name;

    private String price;

    private String productLink;

    private String imageLink;

    private String shopName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(price, that.price) &&
                Objects.equals(productLink, that.productLink) &&
                Objects.equals(imageLink, that.imageLink) &&
                Objects.equals(shopName, that.shopName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, productLink, imageLink, shopName);
    }
}
