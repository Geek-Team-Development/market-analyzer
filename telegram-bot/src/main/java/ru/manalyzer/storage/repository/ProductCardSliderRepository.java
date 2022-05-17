package ru.manalyzer.storage.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.manalyzer.storage.entity.ProductCardSlider;

@Repository
public interface ProductCardSliderRepository extends CrudRepository<ProductCardSlider, String> {
}
