package ru.manalyzer.diginetica.dto;

import ru.manalyzer.dto.ProductDto;

public interface ConverterDto {

    ProductDto convertToDto(DigineticaProductDto digineticaProductDto);
}
