package ru.manalyzer.utility;


import ru.manalyzer.dto.ProductDto;

public interface CardSliderNavigator {

    boolean next();

    boolean previous();

    boolean hasNext();

    boolean hasPrevious();

    int size();

    ProductDto getCurrentProduct();
}
