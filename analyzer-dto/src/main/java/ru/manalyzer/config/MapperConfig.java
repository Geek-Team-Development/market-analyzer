package ru.manalyzer.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.*;
import ru.manalyzer.persist.Product;
import ru.manalyzer.persist.ProductPrice;
import ru.manalyzer.persist.User;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }

    @Bean
    public Mapper<Product, ProductDto> productMapper() {
        return new ProductMapper<>(modelMapper(), Product.class, ProductDto.class);
    }

    @Bean
    public PriceMapper<ProductPrice, Product> productDtoToProductPriceMapper() {
        return new ProductToProductPriceMapper<>(modelMapper(), ProductPrice.class, Product.class);
    }

    @Bean
    public Mapper<User, UserDto> userMapper() {
        return new UserMapper<>(modelMapper(), User.class, UserDto.class);
    }


}
