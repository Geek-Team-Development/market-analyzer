package ru.manalyzer.mapper;

import org.modelmapper.ModelMapper;
import ru.manalyzer.dto.NotificationDto;
import ru.manalyzer.persist.Notification;

public class NotificationMapper<E extends Notification, D extends NotificationDto>
        extends AbstractMapper<E, D> {

    public NotificationMapper(ModelMapper modelMapper, Class<E> entityClass, Class<D> dtoClass) {
        super(modelMapper, entityClass, dtoClass);
    }

    @Override
    public void mapSpecificFields(E source, D destination) {

    }

    @Override
    public void mapSpecificFields(D source, E destination) {

    }
}
