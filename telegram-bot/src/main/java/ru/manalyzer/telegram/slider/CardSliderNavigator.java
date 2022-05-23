package ru.manalyzer.telegram.slider;


import ru.manalyzer.dto.ProductDto;

public interface CardSliderNavigator {

    boolean next();

    boolean previous();

    boolean hasNext();

    boolean hasPrevious();

    int size();

    ProductDto getCurrentProduct();
}
