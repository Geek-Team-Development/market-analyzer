package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OldiResponseDto {

    private Integer totalHits;

    private List<OldiProductDto> products;

    public OldiResponseDto() {
        this.totalHits = 0;
        products = Collections.emptyList();
    }
}
