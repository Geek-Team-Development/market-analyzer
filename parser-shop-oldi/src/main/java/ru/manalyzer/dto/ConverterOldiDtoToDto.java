package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.manalyzer.property.OldiConnectionProperties;

/**
 * Performs conversions between dto objects of the online store and ProductDto
 */
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConverterOldiDtoToDto implements ConverterDto {

    private String shopName;

    private String shopUri;

    @Autowired
    public ConverterOldiDtoToDto(OldiConnectionProperties connectionProperties) {
        this.shopName = connectionProperties.getShopName();
        this.shopUri = connectionProperties.getShopUri();
    }

    /**
     * Converts the dto object of the online store to ProductDto.
     * @param oldiProductDto - dto object of the online store.
     * @return - ProductDto object.
     */
    @Override
    public ProductDto convertToDto(OldiProductDto oldiProductDto) {
        return new ProductDto(
                oldiProductDto.getId(),
                oldiProductDto.getName(),
                oldiProductDto.getPrice(),
                this.shopUri + oldiProductDto.getLink_url(),
                oldiProductDto.getImage_url(),
                this.shopName
        );
    }
}
