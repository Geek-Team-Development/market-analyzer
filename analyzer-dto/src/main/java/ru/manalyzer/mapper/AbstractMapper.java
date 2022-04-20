package ru.manalyzer.mapper;

import lombok.Getter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import ru.manalyzer.dto.AbstractPersistentDto;
import ru.manalyzer.persist.AbstractPersistentObject;

import java.util.Objects;

@Getter
public abstract class AbstractMapper<E extends AbstractPersistentObject, D extends AbstractPersistentDto>
        implements Mapper<E, D> {

    private ModelMapper modelMapper;

    private Class<E> entityClass;

    private Class<D> dtoClass;

    public AbstractMapper(ModelMapper modelMapper, Class<E> entityClass, Class<D> dtoClass) {
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public E toEntity(D dto) {
        return Objects.isNull(dto) ? null : modelMapper.map(dto, entityClass);
    }

    @Override
    public D toDto(E entity) {
        return Objects.isNull(entity) ? null : modelMapper.map(entity, dtoClass);
    }

    public Converter<E, D> toDtoConverter() {
        return context -> {
            E source = context.getSource();
            D destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public Converter<D, E> toEntityConverter() {
        return context -> {
            D source = context.getSource();
            E destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    public abstract void mapSpecificFields(E source, D destination);

    public abstract void mapSpecificFields(D source, E destination);
}
