package ru.manalyzer.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.manalyzer.dto.ProductDto;

@Getter
@Setter
public class ProductUpdateDto {
    private ProductDto oldProductDto;
    private ProductDto newProductDto;
}
