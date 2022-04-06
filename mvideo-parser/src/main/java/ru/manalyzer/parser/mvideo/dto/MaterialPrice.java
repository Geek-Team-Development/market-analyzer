package ru.manalyzer.parser.mvideo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MaterialPrice {
    private Price price;
    private String productId;
}
