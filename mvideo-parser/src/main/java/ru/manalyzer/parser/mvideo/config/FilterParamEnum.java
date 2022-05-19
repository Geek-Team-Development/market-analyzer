package ru.manalyzer.parser.mvideo.config;

public enum FilterParamEnum {

    AVAILABLE_TO_SALE("Только в наличии", "-9");

    private final String desc;
    private final String code;

    FilterParamEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    @Override
    public String toString() {
        return "\"" + desc + "\",\"" + code + "\"";
    }
}
