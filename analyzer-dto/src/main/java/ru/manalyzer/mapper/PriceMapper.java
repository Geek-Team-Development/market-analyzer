package ru.manalyzer.mapper;

public interface PriceMapper<E, D> {

    E toProductPrice(D product);

}
