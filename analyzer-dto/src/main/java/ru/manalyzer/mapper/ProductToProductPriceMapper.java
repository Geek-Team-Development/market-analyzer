package ru.manalyzer.mapper;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;

public class ProductToProductPriceMapper<E extends ProductPrice, D extends Product> implements PriceMapper<E, D> {

    private ModelMapper modelMapper;

    private Class<E> priceClass;

    private Class<D> productClass;

    public ProductToProductPriceMapper(ModelMapper modelMapper, Class<E> priceClass, Class<D> productClass) {
        this.modelMapper = modelMapper;
        this.priceClass = priceClass;
        this.productClass = productClass;
    }

    public void mapSpecificFields(D source, E destination) {
        destination.setProductId(source.getId());
        destination.setDate(new Date());
    }

    @Override
    public E toProductPrice(D product) {
        return Objects.isNull(product) ? null : modelMapper.map(product, priceClass);
    }

    public Converter<D, E> toPriceConverter() {
        return context -> {
            D source = context.getSource();
            E destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    @PostConstruct
    public void initMapper() {
        this.modelMapper.createTypeMap(this.productClass, this.priceClass)
                .addMappings(m -> m.skip(ProductPrice::setId))
                .addMappings(m -> m.skip(ProductPrice::setProductId))
                .setPostConverter(toPriceConverter());
    }
}
