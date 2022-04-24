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
public class ProductDto extends AbstractPersistentDto {

//    private String id;

    private String name;

    private String price;

    private String productLink;

    private String imageLink;

    private String shopName;

    public ProductDto(String id, String name, String price, String productLink, String imageLink, String shopName) {
        super(id);
        this.name = name;
        this.price = price;
        this.productLink = productLink;
        this.imageLink = imageLink;
        this.shopName = shopName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(this.getId(), that.getId()) &&
                Objects.equals(name, that.name) &&
                Objects.equals(price, that.price) &&
                Objects.equals(productLink, that.productLink) &&
                Objects.equals(imageLink, that.imageLink) &&
                Objects.equals(shopName, that.shopName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), name, price, productLink, imageLink, shopName);
    }
}
