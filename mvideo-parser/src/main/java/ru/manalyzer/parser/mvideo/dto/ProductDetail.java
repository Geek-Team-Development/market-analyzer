package ru.manalyzer.parser.mvideo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProductDetail {
    private String productId;
    private String image;
    private String name;
    private String nameTranslit;
}
