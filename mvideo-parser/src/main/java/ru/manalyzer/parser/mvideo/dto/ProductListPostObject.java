package ru.manalyzer.parser.mvideo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ProductListPostObject {
    private boolean brand = true;
    private boolean category = true;
    private List<String> mediaTypes = Collections.singletonList("images");
    private boolean multioffer = true;
    private List<String> productIds;
    private PropertiesConfig propertiesConfig = new PropertiesConfig();
    private List<String> propertyTypes = Collections.singletonList("KEY");
    private boolean status = true;

    public ProductListPostObject(List<String> productIds) {
        this.productIds = productIds;
    }
}
