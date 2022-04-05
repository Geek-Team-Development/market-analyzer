package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.manalyzer.property.OldiConnectionProperties;

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
