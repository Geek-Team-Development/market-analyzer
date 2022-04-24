package ru.manalyzer.mapper;

import org.modelmapper.ModelMapper;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.persist.Product;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

public class ProductMapper<E extends Product, D extends ProductDto> extends AbstractMapper<E, D> {

    public ProductMapper(ModelMapper modelMapper, Class<E> entityClass, Class<D> dtoClass) {
        super(modelMapper, entityClass, dtoClass);
    }

    @Override
    public void mapSpecificFields(E source, D destination) {
        destination.setId(source.getProductShopId());
        destination.setPrice(source.getCost().toString());
    }

    @Override
    public void mapSpecificFields(D source, E destination) {
        destination.setProductShopId(source.getId());
        destination.setCost(new BigDecimal(source.getPrice()));
    }

    @PostConstruct
    public void initMapper() {
        this.getModelMapper().createTypeMap(this.getEntityClass(), this.getDtoClass())
                .addMappings(m -> m.skip(ProductDto::setId))
                .addMappings(m -> m.skip(ProductDto::setPrice))
                .setPostConverter(toDtoConverter());
        this.getModelMapper().createTypeMap(this.getDtoClass(), this.getEntityClass())
                .addMappings(m -> m.skip(Product::setId))
                .addMappings(m -> m.skip(Product::setProductShopId))
                .addMappings(m -> m.skip(Product::setCost))
                .setPostConverter(toEntityConverter());
    }
}
