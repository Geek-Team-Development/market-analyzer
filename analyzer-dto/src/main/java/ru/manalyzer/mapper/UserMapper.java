package ru.manalyzer.mapper;

import org.modelmapper.ModelMapper;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.persist.User;

import javax.annotation.PostConstruct;

public class UserMapper<E extends User, D extends UserDto>
        extends AbstractMapper<E, D> {

    public UserMapper(ModelMapper modelMapper, Class<E> entityClass, Class<D> dtoClass) {
        super(modelMapper, entityClass, dtoClass);
    }

    @Override
    public void mapSpecificFields(E source, D destination) {

    }

    @Override
    public void mapSpecificFields(D source, E destination) {

    }

    @PostConstruct
    public void initMapper() {
        this.getModelMapper().createTypeMap(this.getEntityClass(), this.getDtoClass())
                .addMappings(m -> m.skip(UserDto::setPassword))
                .setPostConverter(toDtoConverter());
        this.getModelMapper().createTypeMap(this.getDtoClass(), this.getEntityClass())
                .addMappings(m -> m.skip(User::setPassword))
                .setPostConverter(toEntityConverter());
    }
}
