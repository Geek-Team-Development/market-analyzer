package ru.manalyzer.parser.mvideo.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterParam {
    private FilterParamEnum filterParamEnum = FilterParamEnum.AVAILABLE_TO_SALE;
    private String yes = "Да";

    @Override
    public String toString() {
        return "[" + filterParamEnum.toString() + ",\"" + yes + "\"]";
    }
}
