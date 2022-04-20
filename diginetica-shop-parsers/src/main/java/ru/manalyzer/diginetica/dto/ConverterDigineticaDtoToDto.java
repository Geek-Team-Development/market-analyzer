package ru.manalyzer.diginetica.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.manalyzer.diginetica.property.DigineticaConnectionProperties;
import ru.manalyzer.dto.ProductDto;

/**
 * Performs conversions between dto objects of the online store and ProductDto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConverterDigineticaDtoToDto implements ConverterDto {

    private String shopName;

    private String shopUri;

    public ConverterDigineticaDtoToDto(DigineticaConnectionProperties connectionProperties) {
        this.shopName = connectionProperties.getShopName();
        this.shopUri = connectionProperties.getShopUri();
    }

    /**
     * Converts the dto object of the online store to ProductDto.
     *
     * @param digineticaProductDto - dto object of the online store.
     * @return - ProductDto object.
     */
    @Override
    public ProductDto convertToDto(DigineticaProductDto digineticaProductDto) {
        return new ProductDto(
                digineticaProductDto.getId(),
                digineticaProductDto.getName(),
                digineticaProductDto.getPrice(),
                this.shopUri + digineticaProductDto.getLink_url(),
                digineticaProductDto.getImage_url(),
                this.shopName
        );
    }
}
