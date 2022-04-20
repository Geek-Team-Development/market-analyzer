package ru.manalyzer.diginetica.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DigineticaResponseDto {

    private Integer totalHits;

    private List<DigineticaProductDto> products;

    public DigineticaResponseDto() {
        this.totalHits = 0;
        products = Collections.emptyList();
    }
}
