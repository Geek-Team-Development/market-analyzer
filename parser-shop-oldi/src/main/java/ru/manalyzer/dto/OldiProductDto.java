package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OldiProductDto {

    private String id;

    private String name;

    private String price;

    private Double score;

    private String link_url;

    private String image_url;
}
