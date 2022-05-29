package ru.manalyzer.mapper;

import org.modelmapper.ModelMapper;
import ru.manalyzer.dto.NotifyMessageDto;
import ru.manalyzer.persist.NotifyMessage;

public class NotifyMessageMapper<E extends NotifyMessage, D extends NotifyMessageDto> extends AbstractMapper<E, D> {

    public NotifyMessageMapper(ModelMapper modelMapper, Class<E> entityClass, Class<D> dtoClass) {
        super(modelMapper, entityClass, dtoClass);
    }

    @Override
    public void mapSpecificFields(E source, D destination) {

    }

    @Override
    public void mapSpecificFields(D source, E destination) {

    }
}
